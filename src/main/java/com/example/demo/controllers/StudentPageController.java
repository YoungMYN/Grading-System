package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.DataBaseHandler;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import org.apache.poi.ss.usermodel.Workbook;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class StudentPageController implements Initializable {
    @FXML
    private ComboBox<String> info;
    @FXML
    protected void showAllMarks(){
        System.out.println("1");
        DataBaseHandler baseHandler = new DataBaseHandler();
        Workbook workbook = baseHandler.getAllMarksInExcel(Const.STUDENT_NAME);
        System.out.println("1");
        Path path = Paths.get("mymarks.xlsx");
        if(Files.exists(path)){
            File file =  new File(String.valueOf(path));
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("mymarks.xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            desktop.open(new File(String.valueOf(path)));

        } catch (IOException e) {
            e.printStackTrace();
        }

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
