package com.smeanox.apps.flatplanmgr;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

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

    @FXML
    private FlowPane StoriesContainer;

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

            initStories();
        }
    }

    void initStories(){
        for (Story story : mainView.getFlatPlan().getStories()) {
            addStoryControl(story);
        }
    }

    public void addStoryControl(Story story){
        FlatPlan flatPlan = mainView.getFlatPlan();

        try {
            FXMLLoader loader = new FXMLLoader(MainView.class.getResource("StoryControl.fxml"));
            StoryControlController controller = new StoryControlController(story);
            loader.setController(controller);
            Pane pane = loader.load();
            controller.setAuthors(flatPlan.getAuthors());
            controller.setCategories(flatPlan.getCategories());
            controller.initListeners();

            StoriesContainer.getChildren().add(pane);
        } catch (IOException e) {
            e.printStackTrace();
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
        AuthorList.getItems().add(new Author("new", "author", "", ""));
    }

    @FXML
    void addCategory(ActionEvent event) {
        CategoryList.getItems().add(new Category("New category"));
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
    void upAuthor(ActionEvent event) {
        upList(AuthorList);
    }

    @FXML
    void upCategory(ActionEvent event) {
        upList(CategoryList);
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
