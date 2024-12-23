package com.example.demo3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

import java.util.Date;
import java.util.Random;

public class addDeviceController {

    // Switch page
    @FXML
    private void addCancel() throws IOException {
        adGuiApplication.setRoot("home-page");
    }


    //button sets and transfers from buttons and textfields to data types for sql database injections to be added here
    // =)
    @FXML
    private TableView<apparaatObj> adTafel;
    @FXML
    public TableColumn<apparaatObj, Integer> adID;
    @FXML
    public TableColumn<apparaatObj, String> installD;
    @FXML
    public TableColumn<apparaatObj, String> locatie;
    @FXML
    public TableColumn<apparaatObj, String> beschrijving;
    @FXML
    public TableColumn<apparaatObj, Double> stofWaarde;
    @FXML
    public TableColumn<apparaatObj, Boolean> gps;
    @FXML
    public TableColumn<apparaatObj, Integer> gpsId;
    @FXML
    private TextField inputDeviceId;
    @FXML
    private TextField inputHardware;
    @FXML
    private TextField inputPlaatsnaam;
    @FXML
    private TextField inputBeschrijving;
    @FXML
    private TextField inputGpsId;
    @FXML
    private CheckBox inputGpsB;

    adGuiController gui = new adGuiController();
    @FXML
    public void nieuwApparaat() {
        adGuiController gui = new adGuiController();
        int id = Integer.parseInt(inputDeviceId.getText());
        String hardware = inputHardware.getText();
        String plaats = inputPlaatsnaam.getText();
        String beschrijving = inputBeschrijving.getText();
        boolean gps = false;
        int gpsId = 0;
        if (!(inputGpsId.getText().isEmpty())) {
            gpsId = Integer.parseInt(inputGpsId.getText());
            gui.sqlAddGps(gpsId);
        }
        if (inputGpsB.isSelected()) {
            gps = true;
        }
        gui.sqlAddDevice(id, hardware, plaats, beschrijving, gps, gpsId);
        initialize();
    }

    // Data and Random Generators
    Random random = new Random();
    Date date = new Date();

    ObservableList<apparaatObj> list = FXCollections.observableArrayList();
    adGuiController func = new adGuiController();
    @FXML
    public void initialize() {
        list = func.getAdDeviceData();
        // Bind the TableView to the shared list
        adTafel.setItems(list);
        // Initialize the table columns
        makeTableCells();
    }

    // Configures the table columns
    public void makeTableCells() {
        adID.setCellValueFactory(new PropertyValueFactory<>("adID"));
        installD.setCellValueFactory(new PropertyValueFactory<>("installD"));
        locatie.setCellValueFactory(new PropertyValueFactory<>("locatie"));
        beschrijving.setCellValueFactory(new PropertyValueFactory<>("beschrijving"));
        stofWaarde.setCellValueFactory(new PropertyValueFactory<>("stofWaarde"));
        gps.setCellValueFactory(new PropertyValueFactory<>("gps"));
        gpsId.setCellValueFactory(new PropertyValueFactory<>("gpsId"));
    }
}
