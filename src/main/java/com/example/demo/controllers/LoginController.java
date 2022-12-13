package com.example.demo.controllers;

import com.example.demo.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import static com.example.demo.Helper.*;


public class LoginController{
    @FXML
    private Text error;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    @FXML
    protected void login(ActionEvent event){
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        if(ARE_TEACHER==1){
            if(dataBaseHandler.checkTeacher(email.getText(), password.getText())){
                System.out.println("success login by teacher");
                TEACHER_NAME = dataBaseHandler.getTeacherName(email.getText());
                TEACHER_SUBJECT = dataBaseHandler.getTeacherSubject(email.getText());
                setScene(event,"/com/example/demo/AddOrCheck.fxml");
            }
            else {
                error.setVisible(true);
                email.setText("");
                password.setText("");
            }
        }
        else if(ARE_TEACHER == 0){
            if(dataBaseHandler.checkStudent(email.getText(), password.getText())){
                System.out.println("success login by student");
                STUDENT_NAME = dataBaseHandler.getStudentName(email.getText());
                setScene(event,"/com/example/demo/StudentPage.fxml");
            }
            else {
                error.setVisible(true);
                email.setText("");
                password.setText("");
            }
        }
    }
    @FXML
    protected void home(ActionEvent event){
        setScene(event,"/com/example/demo/StartPage.fxml");
    }
}
