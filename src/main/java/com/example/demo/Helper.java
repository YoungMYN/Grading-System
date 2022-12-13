package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Helper {
    public static int HAVE_ERROR = 0;
    public static int ARE_TEACHER = 0;
    public static String STUDENT_NAME = null;
    public static String TEACHER_NAME = null;
    public static String TEACHER_SUBJECT = null;

    public static void setScene(ActionEvent event,String path){
        Stage stage;
        Scene scene;
        Parent root = null;
        try {
            root = FXMLLoader.load((Objects.requireNonNull(Helper.class.getResource(path))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static String md5Custom(String st) {
        MessageDigest messageDigest;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        StringBuilder md5Hex = new StringBuilder(bigInt.toString(16));

        while( md5Hex.length() < 32 ){
            md5Hex.insert(0, "0");
        }

        return md5Hex.toString();
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
        if(clip!=null) {
            clip.setFramePosition(0);
            clip.start();
        }
        //хуятину заменить на try with recourses
        try {
            if (ais != null) {
                ais.close();
            }
            if (clip != null) {
                clip.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
