package com.smeanox.apps.flatplanmgr;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Controller for MainView
 */
public class MainViewController {

    MainView mainView;

    @FXML
    private MenuItem NewMenuItem;

    @FXML
    private MenuItem OpenMenuItem;

    @FXML
    private MenuItem SaveMenuItem;

    @FXML
    private MenuItem SaveAsMenuItem;

    @FXML
    private MenuItem CloseMenuItem;

    @FXML
    private MenuItem UploadMenuItem;

    @FXML
    private MenuItem DownloadMenuItem;

    @FXML
    private Button StoriesAdd;

    @FXML
    private Button StoriesSort;

    @FXML
    private ListView<Author> AuthorList;

    @FXML
    private Button AuthorAdd;

    @FXML
    private Button AuthorDel;

    @FXML
    private Button AuthorUp;

    @FXML
    private Button AuthorDown;

    @FXML
    private TextField AuthorFirstName;

    @FXML
    private TextField AuthorLastName;

    @FXML
    private TextField AuthorRole;

    @FXML
    private TextField AuthorMail;

    @FXML
    private ListView<Category> CategoryList;

    @FXML
    private Button CategoryAdd;

    @FXML
    private Button CategoryDel;

    @FXML
    private Button CategoryUp;

    @FXML
    private Button CategoryDown;

    @FXML
    private TextField CategoryName;

    @FXML
    private FlowPane StoriesContainer;

    private Map<Node, StoryControlController> nodeToStoryControlController;

    public MainViewController(MainView mainView) {
        nodeToStoryControlController = new HashMap<>();
        this.mainView = mainView;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initListeners(){
        CategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null){
                return;
            }
            CategoryName.setText(newValue.getName());
        });

        AuthorList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null){
                return;
            }
            AuthorFirstName.setText(newValue.getFirstName());
            AuthorLastName.setText(newValue.getLastName());
            AuthorRole.setText(newValue.getRole());
            AuthorMail.setText(newValue.getMailAddress());
        });

        CategoryName.textProperty().addListener(new TextFieldUpdate<>(CategoryList, Category::setName, Category::getName));
        AuthorFirstName.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setFirstName, Author::getFirstName));
        AuthorLastName.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setLastName, Author::getLastName));
        AuthorRole.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setRole, Author::getRole));
        AuthorMail.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setMailAddress, Author::getMailAddress));

        setItems();
    }

    @FXML
    void closeAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void openAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open flat plan");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showOpenDialog(mainView.getPrimaryStage());
        if(file != null){
            openFile(file);
        }
    }

    @FXML
    void saveAction(ActionEvent event) {
        if(mainView.getFlatPlan() == null){
            return;
        }
        if(mainView.getFlatPlan().getFile() != null){
            mainView.getFlatPlan().save(mainView.getFlatPlan().getFile(), false);
        } else {
            saveAsAction(event);
        }
    }

    @FXML
    void saveAsAction(ActionEvent event) {
        if(mainView.getFlatPlan() == null){
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save flat plan");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        if(mainView.getFlatPlan().getFile() != null) {
            fileChooser.setInitialDirectory(mainView.getFlatPlan().getFile().getParentFile());
            fileChooser.setInitialFileName(mainView.getFlatPlan().getFile().getName());
        }
        File file = fileChooser.showSaveDialog(mainView.getPrimaryStage());
        if(file != null){
            mainView.getFlatPlan().save(file, false);
        }

        updateTitle();
    }

    @FXML
    void sortStories(ActionEvent event) {
        mainView.getFlatPlan().getStories().sort((o1, o2) -> o1.getStart() - o2.getStart());
    }

    @FXML
    void addAuthor(ActionEvent event) {
        AuthorList.getItems().add(new Author("new", "author", "", ""));
    }

    @FXML
    void addCategory(ActionEvent event) {
        CategoryList.getItems().add(new Category("New category"));
    }

    @FXML
    void addStory(ActionEvent event) {
        mainView.getFlatPlan().getStories().add(new Story());
    }

    @FXML
    void deleteAuthor(ActionEvent event) {
        if(AuthorList.getSelectionModel().getSelectedItem() == null){
            return;
        }
        AuthorList.getItems().remove(AuthorList.getSelectionModel().getSelectedItem());
    }

    @FXML
    void deleteCategory(ActionEvent event) {
        if(CategoryList.getSelectionModel().getSelectedItem() == null){
            return;
        }
        CategoryList.getItems().remove(CategoryList.getSelectionModel().getSelectedItem());
    }

    @FXML
    void downAuthor(ActionEvent event) {
        downList(AuthorList);
    }

    @FXML
    void downCategory(ActionEvent event) {
        downList(CategoryList);
    }

    @FXML
    void downloadAction(ActionEvent event) {
        String defaultValue = "";
        if(mainView.getFlatPlan() != null && mainView.getFlatPlan().getFile() != null) {
            defaultValue = mainView.getFlatPlan().getFile().getName();
            defaultValue = defaultValue.substring(0, defaultValue.lastIndexOf('.'));
        }
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Plan name");
        dialog.setHeaderText("Plan name");
        dialog.setContentText("Please enter the plan name");

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            try {
                Path tempDirectory = Files.createTempDirectory("fpm_offline");
                File tmpFile = new File(tempDirectory.toFile(), result.get() + ".csv");

                File authorsTmpFile = FlatPlan.createAuthorsFile(tmpFile);
                if (mainView.getApiWrapper().download(result.get() + ".csv", tmpFile, authorsTmpFile)) {
                    System.out.println(tmpFile.getAbsolutePath());
                    openFile(tmpFile);
                } else {
                    displayExceptionAlert(mainView.getApiWrapper().getLastException());
                }

                if (!authorsTmpFile.delete()){
                    System.err.println("Could not delete tmp file: " + authorsTmpFile.getAbsolutePath());
                }
                if (!tmpFile.delete()){
                    System.err.println("Could not delete tmp file: " + tmpFile.getAbsolutePath());
                }
                tempDirectory.toFile().deleteOnExit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void newAction(ActionEvent event) {
        mainView.setFlatPlan(new FlatPlan());

        setItems();
    }

    @FXML
    void upAuthor(ActionEvent event) {
        upList(AuthorList);
    }

    @FXML
    void upCategory(ActionEvent event) {
        upList(CategoryList);
    }

    @FXML
    void uploadAction(ActionEvent event) {
        if(mainView.getFlatPlan() == null){
            return;
        }

        String defaultValue = "";
        if(mainView.getFlatPlan().getFile() != null) {
            defaultValue = mainView.getFlatPlan().getFile().getName();
            defaultValue = defaultValue.substring(0, defaultValue.lastIndexOf('.'));
        }
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Plan name");
        dialog.setHeaderText("Plan name");
        dialog.setContentText("Please enter the plan name");

        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            try {
                File tmpFile = File.createTempFile("fpm_offline", ".csv");

                mainView.getFlatPlan().save(tmpFile, true);

                File authorsTmpFile = FlatPlan.createAuthorsFile(tmpFile);
                if(!mainView.getApiWrapper().upload(result.get() + ".csv", tmpFile, authorsTmpFile)){
                    displayExceptionAlert(mainView.getApiWrapper().getLastException());
                }

                if (!authorsTmpFile.delete()){
                    System.err.println("Could not delete tmp file: " + authorsTmpFile.getAbsolutePath());
                }
                if (!tmpFile.delete()){
                    System.err.println("Could not delete tmp file: " + tmpFile.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    <T> void downList(ListView<T> list){
        int selectedIndex = list.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0 && selectedIndex < list.getItems().size() - 1){
            Collections.swap(list.getItems(), selectedIndex, selectedIndex+1);
        }
        list.getSelectionModel().select(selectedIndex + 1);
    }

    <T> void upList(ListView<T> list){
        int selectedIndex = list.getSelectionModel().getSelectedIndex();
        if(selectedIndex > 0 && selectedIndex < list.getItems().size()){
            Collections.swap(list.getItems(), selectedIndex, selectedIndex-1);
        }
        list.getSelectionModel().select(selectedIndex - 1);
    }

    private class TextFieldUpdate<T> implements ChangeListener<String>{

        private ListView<T> listView;
        private TextFieldUpdateSetValue<T> textFieldUpdateSetValue;
        private TextFieldUpdateGetValue<T> textFieldUpdateGetValue;

        public TextFieldUpdate(ListView<T> listView, TextFieldUpdateSetValue<T> textFieldUpdateSetValue,
                               TextFieldUpdateGetValue<T> textFieldUpdateGetValue) {
            this.listView = listView;
            this.textFieldUpdateSetValue = textFieldUpdateSetValue;
            this.textFieldUpdateGetValue = textFieldUpdateGetValue;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(listView.getSelectionModel().getSelectedItem() == null
                    || !textFieldUpdateGetValue.getValue(listView.getSelectionModel().getSelectedItem()).equals(oldValue)){
                return;
            }
            textFieldUpdateSetValue.setValue(listView.getSelectionModel().getSelectedItem(), newValue);

            // dirty hack to update list view
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            listView.getItems().set(selectedIndex, listView.getItems().get(selectedIndex));
            listView.getSelectionModel().select(selectedIndex);
        }
    }

    private interface TextFieldUpdateSetValue<T>{
        void setValue(T object, String value);
    }

    private interface TextFieldUpdateGetValue<T>{
        String getValue(T object);
    }

    private void openFile(File file) {
        try {
            mainView.setFlatPlan(new FlatPlan(file));
        } catch (IOException e) {
            displayExceptionAlert(e);
        }

        setItems();
    }

    private void setItems(){
        AuthorList.setItems(mainView.getFlatPlan().getAuthors());
        CategoryList.setItems(mainView.getFlatPlan().getCategories());

        initStories();

        updateTitle();
    }

    private void updateTitle(){
        mainView.getPrimaryStage().setTitle("FlatPlanMgr - "
                + (mainView.getFlatPlan().getFile() != null ? mainView.getFlatPlan().getFile().getName() : "New File"));
    }

    void initStories(){
        StoriesContainer.getChildren().clear();
        for (Story story : mainView.getFlatPlan().getStories()) {
            addStoryControl(story);
        }
        mainView.getFlatPlan().getStories().addListener((ListChangeListener<Story>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (Story s : c.getRemoved()) {
                        removeStoryControl(s);
                    }
                }
                if (c.wasAdded()) {
                    for (Story s : c.getAddedSubList()) {
                        addStoryControl(s);
                    }
                }
                if (c.wasPermutated()) {
                    List<Node> copy = new ArrayList<>(StoriesContainer.getChildren());
                    for (int i = 0; i < c.getList().size(); i++) {
                        if (c.getPermutation(i) != i) {
                            StoriesContainer.getChildren().remove(copy.get(i));
                            StoriesContainer.getChildren().add(c.getPermutation(i), copy.get(i));
                        }
                    }
                }

                if (!checkOrder()) {
                    System.err.println("Failed hard!!!");
                }
            }
        });
    }

    /**
     * Checks whether the panes are in the same order as in the flat plan
     * @return true if this holds
     */
    private boolean checkOrder(){
        ObservableList<Story> stories = mainView.getFlatPlan().getStories();
        for(int i = 0; i < stories.size(); i++){
            StoryControlController con = nodeToStoryControlController.get(StoriesContainer.getChildren().get(i));
            if(con == null){
                System.err.println(i + ": con == null");
                return false;
            }
            if(con.getStory() != stories.get(i)){
                System.err.println(i + ": story != story");
                return false;
            }
        }
        return true;
    }

    public void addStoryControl(Story story){
        FlatPlan flatPlan = mainView.getFlatPlan();

        try {
            FXMLLoader loader = new FXMLLoader(MainView.class.getResource("StoryControl.fxml"));
            StoryControlController controller = new StoryControlController(flatPlan, story);
            loader.setController(controller);
            Pane pane = loader.load();
            controller.setAuthors(flatPlan.getAuthors());
            controller.setCategories(flatPlan.getCategories());
            controller.initListeners();

            nodeToStoryControlController.put(pane, controller);

            int index = flatPlan.getStories().indexOf(story);
            StoriesContainer.getChildren().add(index, pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeStoryControl(Story story){
        for(Node node : StoriesContainer.getChildren()){
            if(nodeToStoryControlController.containsKey(node) && nodeToStoryControlController.get(node).getStory() == story){
                StoriesContainer.getChildren().remove(node);
                nodeToStoryControlController.remove(node);
                break;
            }
        }
    }


    private void displayExceptionAlert(Exception e){
        if(e != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

}
