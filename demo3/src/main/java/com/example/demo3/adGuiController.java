package com.example.demo3;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import jdk.jfr.Period;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;


public class adGuiController implements Initializable {

    private static Scene scene;

    //initialize fxml "fx:id's" to the tableview and columns.
    @FXML
    public TableView<adApparaat> adTafel;
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
    private Button editRowButton;

    @FXML
    private Label welcomeText;

    @FXML
    public PasswordField inputPassword;

    //knop om row te verwijderen
    @FXML
    private Button deleteRowButton;

    @FXML
    private Button adminButton;

//    @FXML
//    private void navigateToLoginPage() {
//        try {
//            // Make sure the path to your loginPage.fxml is correct
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("com.example.demo3/login.fxml"));
//            Parent root = loader.load();
//
//            // Set up the new stage
//            Stage stage = new Stage();
//            stage.setTitle("Login Page");
//            stage.setScene(new Scene(root, 600, 400));
//            stage.show();
//
//            // Optional: Close the current stage
//            Stage currentStage = (Stage) adminButton.getScene().getWindow();
//            currentStage.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("Error loading login page.");
//        }
//    }



    @FXML
    public void logInPage() throws IOException {
        adGuiApplication.setRoot("logIn");
    }

    //login
    @FXML
    public Button btn3;

    //search bar
    @FXML
    public TextField searchBar;

    //search btn
    public void searchBtn() {
        //user input uit de search bar
        String inputText = searchBar.getText().toLowerCase();

        //filter lijst gebasseerd op locatie
        ObservableList<adApparaat> filteredList = FXCollections.observableArrayList();
        for (adApparaat apparaat : list) {
            if (apparaat.getLocatie().toLowerCase().contains(inputText)) {
                filteredList.add(apparaat);
            }
        }

        // Update the TableView with the filtered list
        adTafel.setItems(filteredList);
    }

    Random random = new Random();
    Date date = new Date();

    @FXML
    ObservableList<adApparaat> list = FXCollections.observableArrayList();

    @FXML
    public void deleteRow() {
        adApparaat selectedRow = adTafel.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            list.remove(selectedRow); // Remove from the ObservableList
            adTafel.setItems(list);  // Update the table
        } else {
            System.out.println("No row selected."); // Optional: Add feedback for no selection
        }
    }

    //assure new table cells are applied with data types for new row(s)
    @FXML
    void makeTableCells() {

        adID.setCellValueFactory(new PropertyValueFactory<adApparaat, Integer>("adID"));
        installD.setCellValueFactory(new PropertyValueFactory<adApparaat, String>("installD"));
        locatie.setCellValueFactory(new PropertyValueFactory<adApparaat, String>("locatie"));
        beschrijving.setCellValueFactory(new PropertyValueFactory<adApparaat, String>("beschrijving"));
        stofWaarde.setCellValueFactory(new PropertyValueFactory<adApparaat, Double>("stofWaarde"));
        gps.setCellValueFactory(new PropertyValueFactory<adApparaat, Boolean>("gps"));
        gpsId.setCellValueFactory(new PropertyValueFactory<adApparaat, Integer>("gpsId"));
    }

    @FXML
    private Label ClickMeLabel;

    @FXML
    private Label ClickMeLabel2;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void onClickFirstBtn() {
        ClickMeLabel.setText("Zuid Holland");
    }

    @FXML
    protected void onClickSecondBtn () {
        ClickMeLabel2.setText("Noord Holland");
    }
    @FXML
    protected void onClickThirdBtn () {
        ClickMeLabel.setText("Brabant");
    }

    @FXML
    protected void onClickFourthBtn () {
        ClickMeLabel.setText("Zeeland");
    }

    //any java initializations implemented right below
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeTableCells();
        list = getAdDeviceData();
        adTafel.setItems(list);
    }

    //sql connect
    private Connection connect() {
        Connection conn = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        // MySQL connection string, pas zonodig het pad aan:
        String connection = "jdbc:mysql://localhost:3306/aquadatabase?serverTimezone=UTC";
        String user = "aquaAdmin";
        String password = "geheim";
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(connection, user, password);
            System.out.println("Connected to sql!!!");
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    //add gps id naar tafel linked met als adDevice een gps heeft (true)
    public void sqlAddGps (int gpsId) {
        String sql = "INSERT INTO gps(gpsId) values (?)";
        try {
            PreparedStatement preparedStatement = connect().prepareStatement(sql);
            preparedStatement.setInt(1, gpsId);
            preparedStatement.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    //voeg apparaat toe met sql injectie
    public void sqlAddDevice(int id, String hardware, String plaats, String beschrijving, boolean gps, int gpsId) {
        String sql = "INSERT INTO addevice values(?, ?, ?, ?, ?, ?, ?)";
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        try {
            PreparedStatement preparedStatement = connect().prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, hardware);
            preparedStatement.setDate(3, sqlDate);
            preparedStatement.setString(4, plaats);
            preparedStatement.setString(5, beschrijving);
            preparedStatement.setBoolean(6, gps);
            if (gpsId != 0) {
                preparedStatement.setInt(7, gpsId);
            }
            else {
                String nully = null;
                preparedStatement.setString(7, nully);
            }
            preparedStatement.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    //SQL reader, pakt de huidige lijst van alle adDevice apparaten en stop ze in een lijst
    //die toegevoegd wordt aan de tafel
    public ObservableList<adApparaat> getAdDeviceData() {
        ObservableList<adApparaat> data = FXCollections.observableArrayList();
        Connection conn = connect();
        if (conn == null) {
            System.err.println("Failed to establish a database connection.");
            return data;
        }

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM addevice")) {

            while (rs.next()) {
                int id = rs.getInt("adId");
                String installDate = rs.getString("installDatum");
                String plaatsnaam = rs.getString("Plaatsnaam");
                String beschrijving = rs.getString("Locatiebeschrijving");
                double ppm = 420.69;
                boolean gps = rs.getBoolean("gpsBoolean");
                int gpsId = rs.getInt("gpsId");
                data.add(new adApparaat(id, installDate, plaatsnaam, beschrijving, ppm, gps, gpsId));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return data;
    }
}