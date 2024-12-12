package com.example.demo3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;

public class adGuiApplication extends Application {

    //scene must be in scope for all methods
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        //set imageIcon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/ADLogo_64px.png")));

        //default sceneStart
        FXMLLoader fxmlLoader = new FXMLLoader(adGuiApplication.class.getResource("home-page.fxml"));
        scene = new Scene(fxmlLoader.load(),800 , 600);
        stage.setTitle("Aqua~Data");
        stage.setScene(scene);
        stage.show();
    }
    //pageLoading setRoots
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    //loadFXML .fxml file loader with formatted strings
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(adGuiApplication.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }


    public static void main(String[] args) {
        launch();
    }
}