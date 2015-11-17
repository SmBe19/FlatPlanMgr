package com.smeanox.apps.flatplanmgr;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        CategoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            CategoryName.setText(newValue.getName());
        });

        AuthorList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            AuthorFirstName.setText(newValue.getFirstName());
            AuthorLastName.setText(newValue.getLastName());
            AuthorRole.setText(newValue.getRole());
            AuthorMail.setText(newValue.getMailAddress());
        });

        CategoryName.textProperty().addListener(new TextFieldUpdate<>(CategoryList, Category::setName));
        AuthorFirstName.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setFirstName));
        AuthorLastName.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setLastName));
        AuthorRole.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setRole));
        AuthorMail.textProperty().addListener(new TextFieldUpdate<>(AuthorList, Author::setMailAddress));
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
        mainView.getFlatPlan().getAuthors().add(new Author("new", "author", "", ""));
    }

    @FXML
    void addCategory(ActionEvent event) {
        mainView.getFlatPlan().getCategories().add(new Category("New category"));
    }

    @FXML
    void deleteAuthor(ActionEvent event) {
        if(AuthorList.getSelectionModel().getSelectedItem() == null){
            return;
        }
        mainView.getFlatPlan().getAuthors().remove(AuthorList.getSelectionModel().getSelectedItem());
    }

    @FXML
    void deleteCategory(ActionEvent event) {
        if(CategoryList.getSelectionModel().getSelectedItem() == null){
            return;
        }
        mainView.getFlatPlan().getCategories().remove(CategoryList.getSelectionModel().getSelectedItem());
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

    private class TextFieldUpdate<T> implements ChangeListener<String>{

        private ListView<T> listView;
        private TextFieldUpdateSetValue<T> textFieldUpdateSetValue;

        public TextFieldUpdate(ListView<T> listView, TextFieldUpdateSetValue<T> textFieldUpdateSetValue) {
            this.listView = listView;
            this.textFieldUpdateSetValue = textFieldUpdateSetValue;
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(CategoryList.getSelectionModel().getSelectedItem() == null){
                return;
            }
            textFieldUpdateSetValue.setValue(listView.getSelectionModel().getSelectedItem(), newValue);
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();

            // dirty hack to update list view
            listView.getItems().set(selectedIndex, listView.getItems().get(selectedIndex));
        }
    }

    private interface TextFieldUpdateSetValue<T>{
        void setValue(T object, String value);
    }

}
