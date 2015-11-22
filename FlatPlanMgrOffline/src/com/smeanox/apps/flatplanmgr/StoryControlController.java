package com.smeanox.apps.flatplanmgr;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * Controller for a Story
 */
public class StoryControlController {

    @FXML
    private TextField FirstPage;

    @FXML
    private TextField StoryLength;

    @FXML
    private TextField StoryTitle;

    @FXML
    private ComboBox<Author> StoryAuthor;

    @FXML
    private ComboBox<Category> StoryCategory;

    @FXML
    private TextField FileFormat;

    @FXML
    private ComboBox<StoryStatus> StoryStatusCombo;

    private Story story;

    public StoryControlController(Story story) {
        this.story = story;
    }

    public void setAuthors(ObservableList<Author> authors){
        StoryAuthor.setItems(authors);
    }

    public void setCategories(ObservableList<Category> categories){
        StoryCategory.setItems(categories);
    }

    public void initListeners(){
        FirstPage.setText(String.valueOf(story.getStart()));
        StoryLength.setText(String.valueOf(story.getLength()));
        StoryTitle.setText(story.getTitle());
        StoryAuthor.getSelectionModel().select(story.getAuthor());
        StoryCategory.getSelectionModel().select(story.getCategory());
        FileFormat.setText(story.getFileFormat());

        StoryStatusCombo.setItems(FXCollections.observableArrayList(StoryStatus.values()));
        StoryStatusCombo.getSelectionModel().select(story.getStatus());

        FirstPage.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newVal = Integer.parseInt(newValue);
                story.setStart(newVal);
            } catch (NumberFormatException e) {
                FirstPage.setText(oldValue);
            }
        });
        StoryLength.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newVal = Integer.parseInt(newValue);
                story.setLength(newVal);
            } catch (NumberFormatException e) {
                StoryLength.setText(oldValue);
            }
        });
        StoryTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            story.setTitle(newValue);
        });
        StoryAuthor.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            story.setAuthor(newValue);
        });
        StoryCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            story.setCategory(newValue);
        });
        StoryStatusCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            story.setStatus(newValue);
        });
    }
}
