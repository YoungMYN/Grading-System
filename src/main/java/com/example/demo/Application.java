package com.example.demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//point of entry
public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        //shows the start scene
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("StartPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Grade system v1.0.1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}