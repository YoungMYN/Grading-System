package com.example.demo.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static com.example.demo.Helper.*;
//simple start page controller
public class StartPageController {
    //init like a teacher or student
    @FXML
    protected void setTeacherMode(ActionEvent event){
        ARE_TEACHER = 1;
        setScene(event,"/com/example/demo/LoginPage.fxml");
    }
    @FXML
    protected void setStudentMode(ActionEvent event){
        ARE_TEACHER = 0;
        setScene(event,"/com/example/demo/LoginPage.fxml");
    }
}
