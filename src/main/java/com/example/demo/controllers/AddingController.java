package com.example.demo.controllers;

import com.example.demo.DataBaseHandler;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import org.controlsfx.control.textfield.*;
//controller for addingPage.fxml
public class AddingController implements Initializable {
    //variable for saving previous choice of user
    private static String previousGroup = null;
    private AutoCompletionBinding autocomplete = null;

    @FXML
    private ComboBox<String> info;
    @FXML
    private RadioButton absent;
    @FXML
    private TextField errorWindow;
    @FXML
    private TextField name;
    @FXML
    private ComboBox<String> groups;
    @FXML
    private ComboBox<Integer> marks;

    @FXML
    protected void onSaveButtonClick(ActionEvent event) {
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        // if we try to add a grade to a non-existent student, it won't happen
        if(!dataBaseHandler.getNamesInGroup(groups.getSelectionModel().getSelectedItem())
                .contains(name.getText())){
            Helper.HAVE_ERROR = 1;
        }

        if(Helper.HAVE_ERROR==0) {
            //going to add a mark/absent flag to database
            if (absent.isSelected()) {
                dataBaseHandler.addMark(name.getText(),
                        groups.getSelectionModel().getSelectedItem(),
                        0);
            } else {
                dataBaseHandler.addMark(name.getText(),
                        groups.getSelectionModel().getSelectedItem(),
                        marks.getSelectionModel().getSelectedItem());
            }
        }
        //playing sound(success/fail) and updating the page for new data
        File sound;
        if(Helper.HAVE_ERROR==0){
            sound= new File("src\\main\\resources\\com\\example\\demo\\audio\\added.wav");
            previousGroup = groups.getSelectionModel().getSelectedItem();
            Helper.setScene(event, "/com/example/demo/pages/AddingPage.fxml");

        }
        else {
            sound= new File("src\\main\\resources\\com\\example\\demo\\audio\\error.wav");
            errorWindow.setVisible(true);
            Helper.HAVE_ERROR=0;
        }
        Helper.playSound(sound);
    }
    //hiding a comboBox with marks (if absent option clicked) to simplify the UI
    @FXML
    protected void onAbsentButtonClick(){
        marks.setVisible(!marks.isVisible());
    }
    //adding AutoCompletion by chosen group
    @FXML
    protected void onGroupSelected(){
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        if(autocomplete!=null) autocomplete.dispose();
        autocomplete = TextFields.bindAutoCompletion(name,
                dataBaseHandler.getNamesInGroup(groups.getSelectionModel().getSelectedItem()));
    }
    //show previous page
    @FXML
    protected void backAction(ActionEvent event){
        Helper.setScene(event, "/com/example/demo/pages/AddOrCheck.fxml");
    }
    //show start page and logout from system
    @FXML
    protected void logout(ActionEvent event){
        autocomplete = null;
        Helper.setScene(event, "/com/example/demo/pages/StartPage.fxml");
    }
    //adding information from database and user
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(Helper.TEACHER_NAME);
        info.getItems().add("logout");

        DataBaseHandler handler = new DataBaseHandler();
        for(String i : handler.getGroupsList()){
            groups.getItems().add(i);
        }
        for (int i = 2; i <= 5; i++) {
            marks.getItems().add(i);
        }

        if(previousGroup!=null){
            groups.getSelectionModel().select(previousGroup);
            onGroupSelected();
        }
    }
}