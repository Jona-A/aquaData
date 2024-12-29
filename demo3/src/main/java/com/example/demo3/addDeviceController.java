package com.example.demo3;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class addDeviceController implements Initializable {
    @FXML
    private TextField inputPlaatsnaam;
    @FXML
    private TextField inputBeschrijving;
    @FXML
    private TextField inputGpsId;
    @FXML
    private CheckBox inputGpsB;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Label deviceTitle;
    @FXML
    Label gpsID;
    @FXML
    private Label hardwareWarning;
    @FXML
    private Label plaatsWarning;
    @FXML
    private Label gpsWarning;

    String options[] = { "AD-Prototype v1.0", "AD-Prototype v1.1 (GPS)", "AD-Prototype v2.0" };

    private adminController admin;

    // close window
    @FXML
    private void addCancel() throws IOException {
        admin.closeAddDevice();
    }

    @FXML
    public void nieuwApparaat() {
        adminController admin = new adminController();
        hardwareWarning.setVisible(false);
        plaatsWarning.setVisible(false);
        gpsWarning.setVisible(false);

        if (choiceBox.getValue() != null && inputPlaatsnaam.getText().length() > 2) {
            boolean gps = false;
            int gpsId = 0;

            String hardware = choiceBox.getValue();
            String plaats = inputPlaatsnaam.getText();
            String beschrijving = inputBeschrijving.getText();

            if (inputGpsB.isSelected() && inputGpsId.getText().isEmpty()) {
                gpsWarning.setVisible(true);
                deviceTitle.setStyle("-fx-text-fill: #FF4000;");
                deviceTitle.setText("Voer een GPS ID in of de-selecteer gps!");
            }
            else {
                if (inputGpsB.isSelected() && !inputGpsId.getText().isEmpty()) {
                    gpsId = Integer.parseInt(inputGpsId.getText());
                    admin.sqlAddGps(gpsId);
                    gps = true;
                }

                admin.sqlAddDevice(hardware, plaats, beschrijving, gps, gpsId);

                try {
                    addCancel();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        else if (choiceBox.getValue() == null) {
            hardwareWarning.setVisible(true);
            if (inputPlaatsnaam.getText().length() < 3) {
                plaatsWarning.setVisible(true);
                deviceTitle.setStyle("-fx-text-fill: #FF4000;");
                deviceTitle.setText("Selecteer een Versie en voer een PlaatsNaam in!");
            }
            else {
                deviceTitle.setStyle("-fx-text-fill: #FF4000;");
                deviceTitle.setText("Selecteer een Device-Versie!");
            }
        }
        else {
            plaatsWarning.setVisible(true);
            deviceTitle.setStyle("-fx-text-fill: #FF4000;");
            deviceTitle.setText("Voer een PlaatsNaam in!");
        }
    }

    public void setAdminController(adminController admin) { this.admin = admin; }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initChoiceBox();
    }

    void initChoiceBox() {
        choiceBox.setItems(FXCollections.observableArrayList(options));
    }

}
