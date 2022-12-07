package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StartPageController implements Initializable {
    @FXML
    protected void setTeacherMode(ActionEvent event){
        Const.ARE_TEACHER = 1;
        Helper.setScene(event,"/com/example/demo/LoginPage.fxml");
    }
    @FXML
    protected void setStudentMode(ActionEvent event){
        Const.ARE_TEACHER = 0;
        Helper.setScene(event,"/com/example/demo/LoginPage.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
