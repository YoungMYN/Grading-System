package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.net.URL;
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
        info.setPromptText(Helper.TEACHER_NAME);
        info.getItems().add("logout");
    }
}
