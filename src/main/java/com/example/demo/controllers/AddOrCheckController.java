package com.example.demo.controllers;

import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.net.URL;
import java.util.ResourceBundle;
//controller for AddOrCheck.fxml (choice between 2 options )
public class AddOrCheckController implements Initializable {
    //box with name of current teacher and logout function
    @FXML
    private ComboBox<String> info;

    //shows scene with adding a new mark
    @FXML
    protected void onAddButtonClick(ActionEvent event){
        Helper.setScene(event, "/com/example/demo/pages/AddingPage.fxml");
    }

    //shows scene with statistics by existing marks
    @FXML
    protected void onCheckButtonClick(ActionEvent event) {
        Helper.setScene(event, "/com/example/demo/pages/StatisticPage.fxml");
    }

    @FXML
    protected void logout(ActionEvent event){
        Helper.setScene(event, "/com/example/demo/pages/StartPage.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(Helper.TEACHER_NAME);
        info.getItems().add("logout");
    }
}
