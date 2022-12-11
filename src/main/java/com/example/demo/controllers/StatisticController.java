package com.example.demo.controllers;


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
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


import javafx.scene.control.TextField;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class StatisticController implements Initializable {
    @FXML
    private ComboBox<String> info;
    @FXML
    private TextField errorWindow;
    @FXML
    private ComboBox<String> statisticGroup;

    @FXML
    protected void onGetExelButtonClick(){
        if(statisticGroup.getSelectionModel().getSelectedItem() == null){
            Helper.HAVE_ERROR =1;
        }
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(statisticGroup.getSelectionModel().getSelectedItem());
        ResultSet statByCurrentSubject = dataBaseHandler.
                getAllStudentsWithMarksByCurrentSubject(statisticGroup.getSelectionModel().getSelectedItem());
        ArrayList<String> columnNames = new ArrayList<>();
        try {
            Row subjectRow = sheet.createRow(0);
            subjectRow.createCell(0).setCellValue(Helper.TEACHER_SUBJECT);
            Row row = sheet.createRow(1);
            ResultSetMetaData metaData = statByCurrentSubject.getMetaData();
            for (int i = 2; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnName(i);
                columnNames.add(name);
                row.createCell(i-2).setCellValue(name);
            }
            int i = 1;
            while (statByCurrentSubject.next()){
                i= i+1;
                Row newRow = sheet.createRow(i);

                for (int j = 0; j < columnNames.size(); j++) {
                    try {
                        if(statByCurrentSubject.getString(columnNames.get(j))==null){
                            newRow.createCell(j)
                                    .setCellValue("");
                        }
                        else {
                            newRow.createCell(j)
                                    .setCellValue(statByCurrentSubject.getInt(columnNames.get(j)));
                        }
                    }
                    catch (SQLDataException|NumberFormatException e){
                        newRow.createCell(j)
                                .setCellValue(statByCurrentSubject.getString(columnNames.get(j)));
                    }
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
                if(desktop!=null) desktop.open(new File(String.valueOf(path)));
                else System.out.println("Unable to connect to desktop");
            } catch (IOException e) {
                Helper.HAVE_ERROR = 1;
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Helper.HAVE_ERROR = 1;
            e.printStackTrace();
        } catch (IOException e) {
            if(e.getMessage().equals("stat.xlsx" +
                    " (The process cannot access the file because it is being used by another process)")){
                System.out.println("file is opening or already open");
            }
            else{
                e.printStackTrace();
            }
        }
        File sound;
        if(Helper.HAVE_ERROR==0){
            sound= new File("src\\main\\resources\\com\\example\\demo\\added.wav");
            errorWindow.setVisible(false);
        }
        else {
            sound= new File("src\\main\\resources\\com\\example\\demo\\error.wav");
            errorWindow.setVisible(true);
            Helper.HAVE_ERROR=0;
        }
        Helper.playSound(sound);
    }
    @FXML
    protected void logout(ActionEvent event){
        Helper.TEACHER_SUBJECT = null;
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }
    @FXML
    protected void backAction(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/AddOrCheck.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(Helper.TEACHER_NAME);
        info.getItems().add("logout");
        DataBaseHandler handler = new DataBaseHandler();
        for(String i : handler.getGroupsList()){
            statisticGroup.getItems().add(i);
        }
    }
}
