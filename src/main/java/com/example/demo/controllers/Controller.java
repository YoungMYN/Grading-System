package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.DataBaseHandler;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import org.controlsfx.control.textfield.*;

// удалить таблицы по группам оставить только данные из них в users перенести

public class Controller implements Initializable {
    private static String previousGroup = null;
    private AutoCompletionBinding acb = null;

    @FXML
    private ComboBox<String> info;
    @FXML
    private ComboBox<String> statisticGroup;
    @FXML
    private RadioButton absent;
    @FXML
    private TextField errorWindow;
    @FXML
    private TextField name;
    @FXML
    private ComboBox<String> groups;
    @FXML
    private ComboBox<Integer> marks;


    @FXML
    protected void onSaveButtonClick(ActionEvent event) {
        //System.out.println(name.getText() + " получил сегодня " + marks.getSelectionModel().getSelectedItem());
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        if(!dataBaseHandler.getNamesInGroup(groups.getSelectionModel().getSelectedItem()).contains(name.getText())){
            Const.HAVE_ERROR = 1;
        }

        if(Const.HAVE_ERROR==0) {
            LocalDate now = LocalDate.now(ZoneId.of("Europe/Moscow"));
            ;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            if (absent.isSelected()) {
                dataBaseHandler.addMark(name.getText(),
                        groups.getSelectionModel().getSelectedItem(),
                        0, date);
            } else {
                dataBaseHandler.addMark(name.getText(),
                        groups.getSelectionModel().getSelectedItem(),
                        marks.getSelectionModel().getSelectedItem(), date);
            }
        }
        File sound;
        if(Const.HAVE_ERROR==0){
            sound= new File("src\\main\\resources\\com\\example\\demo\\added.wav");
            previousGroup = groups.getSelectionModel().getSelectedItem();
            Helper.setScene(event,"/com/example/demo/AddingPage.fxml");

        }
        else {
            sound= new File("src\\main\\resources\\com\\example\\demo\\error.wav");
            errorWindow.setVisible(true);
            Const.HAVE_ERROR=0;
        }
        playSound(sound);
    }
    @FXML
    protected void onAbsentButtonClick(){
        marks.setVisible(!marks.isVisible());
    }
    @FXML
    protected void onGroupSelected(){
        if(acb!=null) acb.dispose();
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        acb = TextFields.bindAutoCompletion(name,
                dataBaseHandler.getNamesInGroup(groups.getSelectionModel().getSelectedItem()));


    }
    @FXML
    protected void backAction(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/AddOrCheck.fxml");

    }
    @FXML
    protected void logout(ActionEvent event){
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }
    public static void playSound(File sound){
        AudioInputStream ais = null;
        try {
            ais = AudioSystem.getAudioInputStream(sound);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        info.setPromptText(Const.TEACHER_NAME);
        info.getItems().add("logout");
        DataBaseHandler handler = new DataBaseHandler();
        for(String i : handler.getGroupsList()){
            groups.getItems().add(i);
        }

        marks.getItems().add(2);
        marks.getItems().add(3);
        marks.getItems().add(4);
        marks.getItems().add(5);
        if(previousGroup!=null){
            groups.getSelectionModel().select(previousGroup);
            onGroupSelected();
        }
    }
}