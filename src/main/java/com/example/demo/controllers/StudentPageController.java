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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static com.example.demo.Helper.*;

public class StudentPageController implements Initializable {
    @FXML
    private ComboBox<String> info;

    // opens an Excel file with all student's marks by all subjects
    @FXML
    protected void showAllMarks(){
        DataBaseHandler baseHandler = new DataBaseHandler();
        Workbook allMarksWorkbook = baseHandler.getAllMarksInExcel(STUDENT_NAME);
        Path path = Paths.get("mymarks.xlsx");
        if(Files.exists(path)){
            File marksFile =  new File(String.valueOf(path));
            marksFile.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream("mymarks.xlsx");
            allMarksWorkbook.write(fos);
            fos.close();
            allMarksWorkbook.close();
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            if (desktop != null) {
                desktop.open(new File(String.valueOf(path)));
            }
            else {
                System.out.println("Unable to connect to desktop");
            }
        } catch (IOException e) {
            if(e.getMessage().equals("mymarks.xlsx" +
                    " (The process cannot access the file because it is being used by another process)")){
                System.out.println("file is opening or already open");
            }
            else{
                e.printStackTrace();
            }
        }

    }
    @FXML
    protected void logout(ActionEvent event){
        STUDENT_NAME = null;
        setScene(event,"/com/example/demo/StartPage.fxml");
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(STUDENT_NAME);
        info.getItems().add("logout");
    }
}
