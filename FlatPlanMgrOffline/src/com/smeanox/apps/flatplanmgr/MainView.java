package com.smeanox.apps.flatplanmgr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * The main view of the FPMgr.
 */
public class MainView extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private FlatPlan flatPlan;
    private APIWrapper apiWrapper;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            apiWrapper = new APIWrapper(new File(APIWrapper.CONFIG_DEFAULT_FILE_NAME));
        } catch (IOException e){
            System.out.println(e.getMessage());
            apiWrapper = new APIWrapper();
        }

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("FlatPlan");

        initRootLayout();
    }

    public void initRootLayout(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainView.class.getResource("MainView.fxml"));
            MainViewController controller = new MainViewController(this);
            loader.setController(controller);
            rootLayout = loader.load();
            controller.initListeners();

            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(MainView.class.getResource("main.css").toString());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public FlatPlan getFlatPlan() {
        return flatPlan;
    }

    public void setFlatPlan(FlatPlan flatPlan) {
        this.flatPlan = flatPlan;
    }

    public APIWrapper getApiWrapper() {
        return apiWrapper;
    }

    public static void main(String[] args){
        launch(args);
    }
}
