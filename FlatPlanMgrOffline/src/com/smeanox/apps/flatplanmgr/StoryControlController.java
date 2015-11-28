package com.smeanox.apps.flatplanmgr;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for a Story
 */
public class StoryControlController {

    public final static String STYLE_CLASS_ERROR_TEXT_FIELD = "textFieldError";

    @FXML
    private Label StoryTitleLabel;

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

    @FXML
    private Button StoryLeft;

    @FXML
    private Button StoryDelete;

    @FXML
    private Button StoryRight;

    private Story story;
    private FlatPlan flatPlan;

    public StoryControlController(FlatPlan flatPlan, Story story) {
        this.flatPlan = flatPlan;
        this.story = story;
    }

    public FlatPlan getFlatPlan() {
        return flatPlan;
    }

    public Story getStory() {
        return story;
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

        updateTitle();

        FirstPage.textProperty().addListener((observable, oldValue, newValue) -> {
            FirstPage.getStyleClass().remove(STYLE_CLASS_ERROR_TEXT_FIELD);
            try {
                int newVal = Integer.parseInt(newValue);
                story.setStart(newVal);
            } catch (NumberFormatException e) {
                // FirstPage.setText(oldValue);
                FirstPage.getStyleClass().add(STYLE_CLASS_ERROR_TEXT_FIELD);
            }
            updateTitle();
        });
        StoryLength.textProperty().addListener((observable, oldValue, newValue) -> {
            StoryLength.getStyleClass().remove(STYLE_CLASS_ERROR_TEXT_FIELD);
            try {
                int newVal = Integer.parseInt(newValue);
                story.setLength(newVal);
            } catch (NumberFormatException e) {
                // StoryLength.setText(oldValue);
                StoryLength.getStyleClass().add(STYLE_CLASS_ERROR_TEXT_FIELD);
            }
            updateTitle();
        });
        StoryTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            story.setTitle(newValue);
            updateTitle();
        });
        StoryAuthor.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            story.setAuthor(newValue);
        });
        StoryCategory.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            story.setCategory(newValue);
        });
        FileFormat.textProperty().addListener((observable, oldValue, newValue) -> {
            story.setFileFormat(newValue);
        });
        StoryStatusCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            story.setStatus(newValue);
        });
    }

    private void updateTitle(){
        StoryTitleLabel.setText(story.getTitle() + " (" + story.getStart() + (story.getLength() > 1 ? " - " + (story.getStart() + story.getLength() - 1) : "") + ")");
    }

    @FXML
    void deleteStory(ActionEvent event) {
        flatPlan.getStories().remove(story);
    }

    @FXML
    void storyLeft(ActionEvent event) {
        int index = flatPlan.getStories().indexOf(story);
        if(index < 0){
            return;
        }
        if(index > 0){
            Collections.swap(flatPlan.getStories(), index, index - 1);
        }
    }

    @FXML
    void storyRight(ActionEvent event) {
        int index = flatPlan.getStories().indexOf(story);
        if(index < 0){
            return;
        }
        if(index < flatPlan.getStories().size() - 1){
            Collections.swap(flatPlan.getStories(), index, index + 1);
        }
    }
}
