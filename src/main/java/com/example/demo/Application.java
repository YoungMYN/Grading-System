package com.example.demo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("AddingPage.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("StartPage.fxml"));
        scene = new Scene(fxmlLoader.load());
        //stage.setTitle("Adding a mark");
        stage.setTitle("Get statistic");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}