package com.example.demo3;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URL;
import java.util.Date;


public class adminController implements Initializable {
    public boolean runPpm = false;

    @FXML
    public TableView<apparaatObj> adTafel;
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
    public TableView<ppmObj> ppmTafel;
    @FXML
    public TableColumn<ppmObj, String> ppmTijd;
    @FXML
    public TableColumn<ppmObj, Float> ppmWaarde;
    @FXML
    public TextField searchBar;
    @FXML
    public LineChart ppmChart;

    // TABLEVIEW LISTS;
    @FXML
    ObservableList<apparaatObj> list = FXCollections.observableArrayList();
    @FXML
    ObservableList<ppmObj> ppmList = FXCollections.observableArrayList();

    //search btn
    public void searchBtn() {
        //user input uit de search bar
        String inputText = searchBar.getText().toLowerCase();

        //filter lijst gebasseerd op locatie
        ObservableList<apparaatObj> filteredList = FXCollections.observableArrayList();
        for (apparaatObj apparaat : list) {
            if (apparaat.getLocatie().toLowerCase().contains(inputText)) {
                filteredList.add(apparaat);
            }
        }
        // Update the TableView with the filtered list
        adTafel.setItems(filteredList);
    }

    @FXML
    public void deleteRow() {
        apparaatObj selectedRow = adTafel.getSelectionModel().getSelectedItem();
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
        adID.setCellValueFactory(new PropertyValueFactory<apparaatObj, Integer>("adID"));
        installD.setCellValueFactory(new PropertyValueFactory<apparaatObj, String>("installD"));
        locatie.setCellValueFactory(new PropertyValueFactory<apparaatObj, String>("locatie"));
        beschrijving.setCellValueFactory(new PropertyValueFactory<apparaatObj, String>("beschrijving"));
        stofWaarde.setCellValueFactory(new PropertyValueFactory<apparaatObj, Double>("stofWaarde"));
        gps.setCellValueFactory(new PropertyValueFactory<apparaatObj, Boolean>("gps"));
        gpsId.setCellValueFactory(new PropertyValueFactory<apparaatObj, Integer>("gpsId"));
    }

    //      PPM TABLEVIEW TABLE CELL MAKER
    @FXML
    void makePpmTableCells() {
        ppmTijd.setCellValueFactory(new PropertyValueFactory<ppmObj, String>("tijdStip"));
        ppmWaarde.setCellValueFactory(new PropertyValueFactory<ppmObj, Float>("ppmWaarde"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeTableCells();
        list = getAdDeviceData();
        adTafel.setItems(list);
        ppmFiller(); //START BACKGROUND TASK FOR FILLING THE PPM TABLEVIEW
    }

    //      SQL CONNECTOR FOR GUI-CONTROLLER
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
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    //      SQL INJECTION ADD GPS ROW
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

    //      SQL INJECT NEW ROW INTO DEVICE
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

    //      DEVICE TABLE FILLER, LISTING OBJECTS!
    public ObservableList<apparaatObj> getAdDeviceData() {
        ObservableList<apparaatObj> data = FXCollections.observableArrayList();
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
                data.add(new apparaatObj(id, installDate, plaatsnaam, beschrijving, ppm, gps, gpsId));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return data;
    }

    public ObservableList<ppmObj> getPpm(int id) {
        ObservableList<ppmObj> data = FXCollections.observableArrayList();
        Connection conn = connect();
        if (conn == null) {
            System.err.println("Failed to establish a database connection.");
            return data;
        }

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT updateTijdStip, ppmWaarde FROM ppmdata where adid = " + id)) {
            while (rs.next()) {
                String ppmDate = rs.getString("updateTijdStip");
                float ppmwaarde = rs.getFloat("ppmWaarde");
                data.add(new ppmObj(ppmDate, ppmwaarde));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return data;
    }

    public List<ppmObj> fetchPpmList(int id) {
        List<ppmObj> dataPoints = new ArrayList<>();

        Connection conn = connect();
        if (conn == null) {
            System.err.println("Failed to establish a database connection.");
            return dataPoints;
        }

        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT updateTijdStip, ppmWaarde FROM ppmdata where adid = " + id + " order by updatetijdstip desc limit 20")) {
            while (rs.next()) {
                String ppmDate = rs.getString("updateTijdStip");
                float ppmwaarde = rs.getFloat("ppmWaarde");
                dataPoints.add(new ppmObj(ppmDate, ppmwaarde));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
        return dataPoints;
    }

    //      PPM TABLE FILLER WITH A SELECT SQL INJECTION FROM THE DATABASE
    public void ppmFiller() {

        SwingWorker<Void, Void> ppmWorker = new SwingWorker<>() {
            @Override
            protected  Void doInBackground() {
                int lastId = 0;
                runPpm = true;
                try {
                    while(runPpm) {
                        apparaatObj currentRow = adTafel.getSelectionModel().getSelectedItem();
                        if(currentRow != null) {
                            Thread.sleep(100);
                            int id = currentRow.getAdID();

                            ppmList = getPpm(id);   //      PUT AQUADATA OBJECT LIST INTO A NEW LIST
                            SwingUtilities.invokeLater(() -> {
                                makePpmTableCells();
                                ppmTafel.setItems(ppmList);
                            });

                            if (lastId != id) {
                                new Thread(() -> {
                                    List<ppmObj> chartList = fetchPpmList(id);

                                    Platform.runLater(() -> {
                                        XYChart.Series<String, Float> series1 = new XYChart.Series<>();
                                        ppmChart.getData().clear();
                                        series1.setName("Apparaat: " + id);
                                        int getal = 1;
                                        for (ppmObj object : chartList) {
                                            String nummer = Integer.toString(getal);
                                            series1.getData().add(new XYChart.Data<>(nummer, object.getPpmWaarde()));
                                            getal++;
                                        }
                                        ppmChart.getData().add(series1);

                                    });
                                }).start();
                                Thread.sleep(100);
                            }
                            lastId = id;
                        }
                        Thread.sleep(500); //      DELAY 1 SECOND
                    }
                }
                catch (Exception e) {
                    System.out.println("ERROR");
                    e.printStackTrace(); // Dit drukt de foutmeldingen af.
                }
                return null;
            }
        };
        ppmWorker.execute();
    }
}