package com.example.demo.controllers;

import com.example.demo.Const;
import com.example.demo.DataBaseHandler;
import com.example.demo.Helper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Text error;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private void login(ActionEvent event){
        DataBaseHandler dataBaseHandler = new DataBaseHandler();
        if(Const.ARE_TEACHER==1){
            if(dataBaseHandler.checkTeacher(email.getText(), Helper.md5Custom(password.getText()))){
                System.out.println("passed");
                Const.TEACHER_NAME = dataBaseHandler.getTeacherName(email.getText());
                Const.TEACHER_SUBJECT = dataBaseHandler.getTeacherSubject(email.getText());
                System.out.println(Const.TEACHER_NAME + Const.TEACHER_SUBJECT);
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
                System.out.println("passed");
                Const.STUDENT_NAME = dataBaseHandler.getStudentName(email.getText());
                System.out.println(Const.STUDENT_NAME);
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
