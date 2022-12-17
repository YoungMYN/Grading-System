package com.example.demo.controllers;

import com.example.demo.DataBaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import static com.example.demo.Helper.*;

//controller for login proses
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
        //if user is a teacher initializing teacher, otherwise trying to initialize a student
        if(ARE_TEACHER==1 & dataBaseHandler.checkUser(email.getText(), password.getText())){
            System.out.println("success login by teacher");
            TEACHER_NAME = dataBaseHandler.getTeacherName(email.getText());
            TEACHER_SUBJECT = dataBaseHandler.getTeacherSubject(email.getText());
            setScene(event, "/com/example/demo/pages/AddOrCheck.fxml");
        }
        else if(ARE_TEACHER == 0 & dataBaseHandler.checkUser(email.getText(), password.getText())){
            System.out.println("success login by student");
            STUDENT_NAME = dataBaseHandler.getStudentName(email.getText());
            STUDENT_MAIL = email.getText();
            setScene(event, "/com/example/demo/pages/StudentPage.fxml");
        }
        //if login failed, shows text of error to user
        else {
            error.setVisible(true);
            email.setText("");
            password.setText("");
        }
    }
    //back to start page
    @FXML
    protected void home(ActionEvent event){
        setScene(event, "/com/example/demo/pages/StartPage.fxml");
    }
}
