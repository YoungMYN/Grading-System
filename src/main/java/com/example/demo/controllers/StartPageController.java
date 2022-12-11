package com.example.demo.controllers;

import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StartPageController {
    @FXML
    protected void setTeacherMode(ActionEvent event){
        Helper.ARE_TEACHER = 1;
        Helper.setScene(event,"/com/example/demo/LoginPage.fxml");
    }
    @FXML
    protected void setStudentMode(ActionEvent event){
        Helper.ARE_TEACHER = 0;
        Helper.setScene(event,"/com/example/demo/LoginPage.fxml");
    }
}
