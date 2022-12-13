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

public class AddingController implements Initializable {

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

        if(!dataBaseHandler.getNamesInGroup(groups.getSelectionModel().getSelectedItem())
                .contains(name.getText())){
            Helper.HAVE_ERROR = 1;
        }

        if(Helper.HAVE_ERROR==0) {
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
        File sound;
        if(Helper.HAVE_ERROR==0){
            sound= new File("src\\main\\resources\\com\\example\\demo\\added.wav");
            previousGroup = groups.getSelectionModel().getSelectedItem();
            Helper.setScene(event,"/com/example/demo/AddingPage.fxml");

        }
        else {
            sound= new File("src\\main\\resources\\com\\example\\demo\\error.wav");
            errorWindow.setVisible(true);
            Helper.HAVE_ERROR=0;
        }
        Helper.playSound(sound);
    }

    @FXML
    protected void onAbsentButtonClick(){
        marks.setVisible(!marks.isVisible());
    }

    @FXML
    protected void onGroupSelected(){
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        if(autocomplete!=null) autocomplete.dispose();
        autocomplete = TextFields.bindAutoCompletion(name,
                dataBaseHandler.getNamesInGroup(groups.getSelectionModel().getSelectedItem()));
    }

    @FXML
    protected void backAction(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/AddOrCheck.fxml");
    }
    @FXML
    protected void logout(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }

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