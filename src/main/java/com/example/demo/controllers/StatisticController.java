package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.DataBaseHandler;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;


import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class StatisticController implements Initializable {
    @FXML
    private ComboBox<String> info;
    @FXML
    private TextField errorWindow;
    @FXML
    private ComboBox<String> statisticGroup;

    //ResultSet sss = dataBaseHandler.getAllStudentsWithMarks(groups.getSelectionModel().getSelectedItem());
    @FXML
    protected void onGetExelButtonClick(){
        if(statisticGroup.getSelectionModel().getSelectedItem() == null){
            Const.HAVE_ERROR =1;
        }
        File sound;
        if(Const.HAVE_ERROR==0){
            sound= new File("src\\main\\resources\\com\\example\\demo\\added.wav");
            errorWindow.setVisible(false);
        }
        else {
            sound= new File("src\\main\\resources\\com\\example\\demo\\error.wav");
            errorWindow.setVisible(true);
            Const.HAVE_ERROR=0;
        }
        Controller.playSound(sound);



        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(statisticGroup.getSelectionModel().getSelectedItem());
        ResultSet sss = dataBaseHandler.getAllStudentsWithMarksByCurrentSubject(statisticGroup.getSelectionModel().getSelectedItem());
        
        ArrayList<String> coloumnNames = new ArrayList<>();
        try {
            Row subjectRow = sheet.createRow(0);
            subjectRow.createCell(0).setCellValue(Const.TEACHER_SUBJECT);

            Row row = sheet.createRow(1);
            ResultSetMetaData rsmd = sss.getMetaData();
            for (int i = 2; i <= rsmd.getColumnCount(); i++) {
                String name = rsmd.getColumnName(i);
                coloumnNames.add(name);
                row.createCell(i-2).setCellValue(name);
            }
            int i = 1;
            while (sss.next()){
                i= i+1;
                Row newRow = sheet.createRow(i);
                for (int j = 0; j < coloumnNames.size(); j++) {
                    newRow.createCell(j).setCellValue(sss.getString(coloumnNames.get(j)));
                }
            }
            Path path = Paths.get("stat.xlsx");
            if(Files.exists(path)){
                File file =  new File(String.valueOf(path));
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream("stat.xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            try {
                desktop.open(new File(String.valueOf(path)));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } catch (SQLException | IOException e) {
            Const.HAVE_ERROR = 1;
            e.printStackTrace();
        }

    }
    @FXML
    protected void logout(ActionEvent event){
        Const.TEACHER_SUBJECT = null;
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }
    @FXML
    protected void backAction(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/AddOrCheck.fxml");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(Const.TEACHER_NAME);
        info.getItems().add("logout");
        DataBaseHandler handler = new DataBaseHandler();
        for(String i : handler.getGroupsList()){
            statisticGroup.getItems().add(i);
        }
    }
}
