package com.example.demo3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;


public class adGuiApplication extends Application {

    public String style1 = "styles.css";
    public String style2 = "styles2.css";
    public String thisStyle = style1;

    public String changeStyle(int option) {
        if (option == 1) {
            thisStyle = style1;
        }
        else if (option == 2)
            thisStyle = style2;

        return thisStyle;
    }
    public String getStyle() {
        return thisStyle;
    }

    //scene must be in scope for all methods
    private static Scene scene;
    adGuiController gui = new adGuiController();

    public static Scene getScene() {
        return scene;
    }

    @Override
    public void start(Stage stage) throws IOException {
        //set imageIcon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/ADLogo_64px.png")));

        //default sceneStart
        FXMLLoader fxmlLoader = new FXMLLoader(adGuiApplication.class.getResource("home-page.fxml"));
        scene = new Scene(fxmlLoader.load(),1100 , 800);
        stage.setTitle("Aqua~Data");
        stage.setScene(scene);
        gui.startSqlTask();
        scene.getStylesheets().add(getClass().getResource(thisStyle).toExternalForm());
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