package com.example.demo3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class LogIn {

    @FXML
    private PasswordField inputPassword;
    @FXML
    private Label wwLabel;
    @FXML
    private Button terugNaarHome;

    @FXML
    private void addCancel() throws IOException {
        adGuiApplication.setRoot("home-page");
    }


    private String password = "milan";

    @FXML
    public void vergelijkKnop() throws IOException {

        String inputP = inputPassword.getText();
        if (password.equals(inputP)) {
            System.out.println("Acces granted.");
            adGuiApplication.setRoot("addDevice");
        }

        else
            wwLabel.setText("Wrong, please try again.");
    }
    @FXML
    public void terugNaarHome() throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(adGuiApplication.class.getResource("home-page.fxml"));
        Stage window = (Stage) terugNaarHome.getScene().getWindow();
        Scene newScene = new Scene(fxmlLoader.load(), 800, 600);
        window.setScene(newScene);
        window.show();
    }
}
