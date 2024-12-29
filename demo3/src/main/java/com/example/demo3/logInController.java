package com.example.demo3;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class logInController implements Initializable {

    public static String username = "admin";
    public static String password = "admin";
    public Label errorText;
    public TextField usernameInput;
    public PasswordField passwordInput;
    private adGuiController gui;

    public void setAdGuiController(adGuiController gui) {
        this.gui = gui;
    }

    int count = 0;

    public void tryLogIn() throws IOException {
        String usernameTry = usernameInput.getText(), passwordTry = passwordInput.getText();
        if (usernameTry.equals(username) && passwordTry.equals(password)) {
            adGuiApplication.setRoot("admin");
            gui.closeLogIn();
        }
        else {
            errorText.setText("Incorrect username or password!");
            count++;
            if (count > 2) {
                gui.closeLogIn();
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void cancelLogIn(ActionEvent actionEvent) {
        gui.closeLogIn();
    }
}
