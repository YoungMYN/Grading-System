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
import java.util.ResourceBundle;


import javafx.scene.control.TextField;
import org.apache.poi.ss.usermodel.*;

//controller for a StatisticPage.fxml, responsible for getting the statistics of the selected group
public class StatisticController implements Initializable {
    @FXML
    private ComboBox<String> info;
    @FXML
    private TextField errorWindow;
    @FXML
    private ComboBox<String> statisticGroup;
    //controller main function, opens Excel file with all marks in selected group
    // (in the subject taught by the current teacher)
    @FXML
    protected void onGetExelButtonClick(){
        if(statisticGroup.getSelectionModel().getSelectedItem() == null){
            Helper.HAVE_ERROR =1;
        }
        else {
            DataBaseHandler dataBaseHandler = new DataBaseHandler();
            Workbook statisticsWorkbook = dataBaseHandler
                    .getAllGroupMarksInExcel(statisticGroup.getSelectionModel().getSelectedItem());
            try{
                //deleting previous file (old saved version)
                Path path = Paths.get("stat.xlsx");
                if (Files.exists(path)) {
                    File statisticsFile = new File(String.valueOf(path));
                    statisticsFile.delete();
                }
                //creating new file
                FileOutputStream fos = new FileOutputStream("stat.xlsx");
                statisticsWorkbook.write(fos);
                //trying to get desktop and open our file
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                try {
                    if (desktop != null) desktop.open(new File(String.valueOf(path)));
                    else System.out.println("Unable to connect to desktop");
                } catch (IOException e) {
                    Helper.HAVE_ERROR = 1;
                    e.printStackTrace();
                }
                fos.close();
                statisticsWorkbook.close();
            }
            catch (IOException e) {
                if (e.getMessage().equals("stat.xlsx" +
                        " (The process cannot access the file because it is being used by another process)")) {
                    System.out.println("file is opening or already open");
                } else {
                    e.printStackTrace();
                }
            }
        }
        //just for fun added 2 sounds for different occasions
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
    //moves user to previous page
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
