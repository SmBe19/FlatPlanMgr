package com.smeanox.apps.flatplanmgr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main view of the FPMgr.
 */
public class MainView extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private FlatPlan flatPlan;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("FlatPlan");

        initRootLayout();
    }

    public void initRootLayout(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainView.class.getResource("MainView.fxml"));
            loader.setController(new MainViewController(this));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
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

    public static void main(String[] args){
        launch(args);
    }
}
