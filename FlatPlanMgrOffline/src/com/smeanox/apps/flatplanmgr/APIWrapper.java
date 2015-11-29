package com.smeanox.apps.flatplanmgr;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides access to the API.
 */
public class APIWrapper {

    private String baseUrl;
    private String username;
    private String password;

    private Exception lastException;

    public final static String CONFIG_DEFAULT_FILE_NAME = "fpm_config.txt";

    private final static String ACTION_LOGIN = "login";
    private final static String ACTION_LOGOUT = "logout";
    private final static String ACTION_UPLOAD = "upload_plan";
    private final static String ACTION_DOWNLOAD = "download_plan";

    private final static String CONNECTION_CHARSET = StandardCharsets.UTF_8.displayName();
    private final static String CRLF = "\r\n";

    private final static String CONFIG_BASE_URL = "base_url";
    private final static String CONFIG_USERNAME = "username";
    private final static String CONFIG_PASSWORD = "password";

    public APIWrapper() {
        CookieManager.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    public APIWrapper(File configFile) throws IOException {
        this();
        readConfig(configFile);
    }

    public APIWrapper(String baseUrl) {
        this();
        this.baseUrl = baseUrl;
    }

    public APIWrapper(String baseUrl, String username, String password) {
        this();
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    public Exception getLastException() {
        return lastException;
    }

    public void readConfig(File configFile) throws IOException {
        if(!configFile.exists()){
            throw new FileNotFoundException("File " + configFile.getAbsolutePath() + " not found");
        }
        Files.lines(configFile.toPath()).forEach(s -> {
            String[] parts = s.split("=", 2);
            if(parts.length != 2){
                return;
            }
            switch(parts[0].toLowerCase()){
                case CONFIG_BASE_URL:
                    baseUrl = parts[1].trim();
                    break;
                case CONFIG_USERNAME:
                    username = parts[1].trim();
                    break;
                case CONFIG_PASSWORD:
                    password = parts[1].trim();
                    break;
            }
        });
    }

    private void checkConfig() throws IOException {
        if(baseUrl == null){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Base URL");
            dialog.setHeaderText("Base URL");
            dialog.setContentText("Please enter the base url");
            Optional<String> result = dialog.showAndWait();
            if(!result.isPresent()){
                throw new IOException("config: missing baseUrl");
            }
            baseUrl = result.get();
        }
        if(username == null){
            Dialog<Pair<String, String>> dialog = new Dialog<>();

            dialog.setTitle("Login Credentials");
            dialog.setHeaderText("Login Credentials");
            dialog.setContentText("Please enter your login credentials");

            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField usernameField = new TextField();
            usernameField.setPromptText("username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("password");

            grid.add(usernameField, 0, 0);
            grid.add(passwordField, 0, 1);

            dialog.getDialogPane().setContent(grid);

            Platform.runLater(usernameField::requestFocus);

            dialog.setResultConverter(param -> {
                if(param == loginButtonType){
                    return new Pair<>(usernameField.getText(), passwordField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            if(!result.isPresent()){
                throw new IOException("config: missing login credentials");
            }

            username = result.get().getKey();
            password = result.get().getValue();
        }
    }

    public boolean logout(){
        try {
            checkConfig();
            makeRequest(createParamsList("action", ACTION_LOGOUT));
        } catch (IOException e) {
            System.err.println("logout: " + e.getMessage());
            lastException = e;
            return false;
        }
        System.out.println("logout");
        return true;
    }

    public boolean login(){
        try {
            checkConfig();
            makeRequest(createParamsList("action", ACTION_LOGIN, "username", username, "password", password));
        } catch (IOException e) {
            System.err.println("login: " + e.getMessage());
            lastException = e;
            return false;
        }
        System.out.println("login");
        return true;
    }

    public boolean upload(String name, File planFile, File authorsFile){
        if(login()) {
            try {
                List<Pair<String, File>> files = new ArrayList<>();
                files.add(new Pair<>("fileUploadPlan", planFile));
                files.add(new Pair<>("fileUploadAuthors", authorsFile));
                String result = makeRequest(createParamsList("action", ACTION_UPLOAD, "plan", name), files);

                System.out.println(result);
            } catch (IOException e) {
                System.err.println("upload: " + e.getMessage());
                lastException = e;
                return false;
            }
            System.out.println("upload");
            logout();
            return true;
        }
        return false;
    }

    public boolean download(String name, File planFile, File authorsFile){
        if(login()) {
            try {
                String result = makeRequest(createParamsList("action", ACTION_DOWNLOAD, "plan", md5(name)));

                String[] parts = result.split("---");

                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(planFile), StandardCharsets.UTF_8));
                writer.println(parts[0].trim());
                writer.close();

                if(parts.length > 1){
                    writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(authorsFile), StandardCharsets.UTF_8));
                    writer.println(parts[1].trim());
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("download: " + e.getMessage());
                lastException = e;
                return false;
            }
            System.out.println("download");
            logout();
            return true;
        }
        return false;
    }

    private String makeRequest(List<Pair<String, String>> params) throws IOException {
        return makeRequest(params, new ArrayList<>());
    }

    private String makeRequest(List<Pair<String, String>> params, List<Pair<String, File>> files) throws IOException {
        URL url = new URL(baseUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String boundary = Long.toHexString(System.currentTimeMillis());

        connection.setDoOutput(true);
        connection.setRequestProperty("Accept-Charset", CONNECTION_CHARSET);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        OutputStream out = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, CONNECTION_CHARSET), true);

        // send parameters
        for(Pair<String, String> param : params) {
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"").append(param.getKey()).append("\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(CONNECTION_CHARSET).append(CRLF);
            writer.append(CRLF).append(param.getValue()).append(CRLF).flush();
        }

        for(Pair<String, File> file : files){
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"").append(file.getKey()).append("\"; filename=\"")
                    .append(file.getValue().getName()).append("\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(CONNECTION_CHARSET).append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(file.getValue().toPath(), out);
            out.flush();
            writer.append(CRLF).flush();
        }

        writer.append("--").append(boundary).append(CRLF).flush();

        int responseCode = connection.getResponseCode();
        if(responseCode != 200){
            InputStream in = connection.getErrorStream();
            if(in != null) {
                throw new IOException("Invalid Request: " + new BufferedReader(new InputStreamReader(in)).readLine());
            } else {
                throw new IOException("Invalid Request: " + responseCode + " " + connection.getResponseMessage());
            }
        }

        InputStream in = connection.getInputStream();

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, CONNECTION_CHARSET));
        for(String line; (line = reader.readLine()) != null;){
            sb.append(line);
            sb.append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String md5(String value){
        try {
            byte[] bytes = MessageDigest.getInstance("MD5").digest(value.getBytes());
            BigInteger bigInt = new BigInteger(1, bytes);
            String hash = bigInt.toString(16);
            while(hash.length() < 32){
                hash = "0" + hash;
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<Pair<String, String>> createParamsList(String ... values){
        List<Pair<String, String>> list = new ArrayList<>();
        String oldValue = null;
        for(String value : values){
            if(oldValue == null){
                oldValue = value;
            } else {
                list.add(new Pair<>(oldValue, value));
                oldValue = null;
            }
        }
        return list;
    }
}
