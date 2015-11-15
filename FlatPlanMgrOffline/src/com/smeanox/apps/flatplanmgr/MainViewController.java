package com.smeanox.apps.flatplanmgr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

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

    public MainViewController(MainView mainView) {
        this.mainView = mainView;
    }

    public MainView getMainView() {
        return mainView;
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void initListeners(){
        CategoryList.getSelectionModel().selectedItemProperty().addListener(observable -> {
            CategoryName.setText(((Category) observable).getName());
        });

        AuthorList.getSelectionModel().selectedItemProperty().addListener(observable -> {
            AuthorFirstName.setText(((Author) observable).getFirstName());
            AuthorLastName.setText(((Author) observable).getLastName());
            AuthorRole.setText(((Author) observable).getRole());
            AuthorMail.setText(((Author) observable).getMailAddress());
        });
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

            AuthorList.setItems(mainView.getFlatPlan().getAuthors());
            CategoryList.setItems(mainView.getFlatPlan().getCategories());
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
        File file = fileChooser.showSaveDialog(mainView.getPrimaryStage());
        if(file != null){
            mainView.getFlatPlan().save(file);
        }
    }

    @FXML
    void addAuthor(ActionEvent event) {

    }

    @FXML
    void addCategory(ActionEvent event) {

    }

    @FXML
    void deleteAuthor(ActionEvent event) {

    }

    @FXML
    void deleteCategory(ActionEvent event) {

    }

    @FXML
    void downAuthor(ActionEvent event) {

    }

    @FXML
    void downCategory(ActionEvent event) {

    }

    @FXML
    void upAuthor(ActionEvent event) {

    }

    @FXML
    void upCategory(ActionEvent event) {

    }

}
