package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentPageController implements Initializable {
    @FXML
    private ComboBox<String> info;
    @FXML
    protected void showAllMarks(){

    }
    @FXML
    protected void logout(ActionEvent event){
        Const.STUDENT_NAME = null;
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(Const.STUDENT_NAME);
        info.getItems().add("logout");
    }
}
