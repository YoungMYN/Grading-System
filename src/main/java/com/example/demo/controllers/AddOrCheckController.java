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
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddOrCheckController implements Initializable {
    @FXML
    private ComboBox<String> info;
    @FXML
    protected void onAddButtonClick(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/AddingPage.fxml");
    }
    @FXML
    protected void onCheckButtonClick(ActionEvent event) {
        Helper.setScene(event,"/com/example/demo/StatisticPage.fxml");
    }
    @FXML
    protected void logout(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(Const.TEACHER_NAME);
        info.setPromptText(Const.TEACHER_NAME);
        info.getItems().add("logout");
    }
}
