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
//an additional class that contains static auxiliary variables and methods used in other classes
public class Helper {
    //stores state of some sql errors in program for tracing
    public static int HAVE_ERROR = 0;
    //stores which type of user active now(student ot teacher)
    public static int ARE_TEACHER = 0;
    //stores name and mail of the active student
    public static String STUDENT_NAME = null;
    public static String STUDENT_MAIL = null;
    //stores name and subject of the active teacher
    public static String TEACHER_NAME = null;
    public static String TEACHER_SUBJECT = null;

    //changes the active scene to another one, the path to which is passed as a string
    public static void setScene(ActionEvent event,String path){
        Stage stage;
        Scene scene;
        Parent root = null;
        try {
            root = FXMLLoader.load((Objects.requireNonNull(Helper.class.getResource(path))));
        } catch (IOException e) {
            System.out.println("Problem accessing pages");
            e.printStackTrace();
        }
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    //encryption with MD5 algorithm
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
    //playing sound file
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
    }
}
