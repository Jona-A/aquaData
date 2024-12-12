package com.example.demo3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Text;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;

public class addDeviceController {

    // Switch page
    @FXML
    private void addCancel() throws IOException {
        adGuiApplication.setRoot("home-page");
    }


    //button sets and transfers from buttons and textfields to data types for sql database injections to be added here
    // =)
    @FXML
    private TableView<adApparaat> adTafel;
    @FXML
    public TableColumn<adApparaat, Integer> adID;
    @FXML
    public TableColumn<adApparaat, String> installD;
    @FXML
    public TableColumn<adApparaat, String> locatie;
    @FXML
    public TableColumn<adApparaat, String> beschrijving;
    @FXML
    public TableColumn<adApparaat, Double> stofWaarde;
    @FXML
    public TableColumn<adApparaat, Boolean> gps;
    @FXML
    public TableColumn<adApparaat, Integer> gpsId;
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

    // ObservableList (shared)
    //ObservableList<adApparaat> list = SharedData.sharedList
    ObservableList<adApparaat> list = FXCollections.observableArrayList();
    adGuiController func = new adGuiController();
    @FXML
    public void initialize() {
        list = func.getAdDeviceData();
        // Bind the TableView to the shared list
        adTafel.setItems(list);
        // Initialize the table columns
        makeTableCells();
    }



//    // Adds a new row to the table
//    @FXML
//    public void addRow() {
//        // Generate random data
//        date = Calendar.getInstance().getTime();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String strDate = dateFormat.format(date);
//
//        int number = random.nextInt(999999);
//        double ppm = random.nextDouble() * 1000.0;
//
//        // Create new device and add to list
//        adApparaat nieuwApp = new adApparaat(number, strDate, "home", "kamer", ppm, false, 0);
//        list.add(nieuwApp);
//    }

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
