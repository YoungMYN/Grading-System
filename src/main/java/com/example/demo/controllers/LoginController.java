package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.DataBaseHandler;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


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
        if(Helper.ARE_TEACHER==1){
            if(dataBaseHandler.checkTeacher(email.getText(), Helper.md5Custom(password.getText()))){
                System.out.println("success login by teacher");
                Helper.TEACHER_NAME = dataBaseHandler.getTeacherName(email.getText());
                Helper.TEACHER_SUBJECT = dataBaseHandler.getTeacherSubject(email.getText());
                Helper.setScene(event,"/com/example/demo/AddOrCheck.fxml");
            }
            else {
                error.setVisible(true);
                email.setText("");
                password.setText("");
            }
        }
        else{
            if(dataBaseHandler.checkStudent(email.getText(), Helper.md5Custom(password.getText()))){
                System.out.println("success login by student");
                Helper.STUDENT_NAME = dataBaseHandler.getStudentName(email.getText());
                Helper.setScene(event,"/com/example/demo/StudentPage.fxml");
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
        Helper.setScene(event,"/com/example/demo/StartPage.fxml");
    }
}
