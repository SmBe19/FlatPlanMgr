package com.smeanox.apps.flatplanmgr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Controller for MainView
 */
public class MainViewController {

    MainView mainView;

    @FXML
    private MenuItem OpenMenuItem;

    @FXML
    private MenuItem SaveMenuItem;

    @FXML
    private MenuItem SaveAsMenuItem;

    @FXML
    private MenuItem CloseMenuItem;

    public MainViewController(MainView mainView) {
        this.mainView = mainView;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
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
            mainView.setFlatPlan(new FlatPlan(file));

            SaveMenuItem.setDisable(false);
            SaveAsMenuItem.setDisable(false);
        }
    }

    @FXML
    void saveAction(ActionEvent event) {
        if(mainView.getFlatPlan() == null){
            return;
        }
        if(mainView.getFlatPlan().getFile() != null){
            mainView.getFlatPlan().save(mainView.getFlatPlan().getFile());
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
        File file = fileChooser.showOpenDialog(mainView.getPrimaryStage());
        if(file != null){
            mainView.getFlatPlan().save(file);
        }
    }
}
