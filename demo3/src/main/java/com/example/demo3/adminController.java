package com.example.demo3;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.net.URL;


public class adminController implements Initializable {
    public boolean runPpm = false;
    private String currentStyle;
    public Label ppmAvgLabel;
    public Button addButton;
    public Button deleteButton;
    public Button resetButton;
    public Button changeButton;
    public Button lightButton;
    public Button updateButton;
    public Button darkButton;

    @FXML
    public TableView<apparaatObj> adTafel;
    @FXML
    public TableColumn<apparaatObj, Integer> adID;
    @FXML
    public TableColumn<apparaatObj, String> adVersie;
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
    @FXML
    public Label avgPpm;

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

    public void changeBeschrijving(ActionEvent actionEvent) {
        String sql = "update addevice set locatieBeschrijving = ? where adid = ?";
        Connection conn = connect();
        String changeBeschrijving = searchBar.getText();
        apparaatObj selectedRow = adTafel.getSelectionModel().getSelectedItem();
        int thisId = 0;
        try {
            thisId = selectedRow.getAdID();
        } catch (Exception e) {
             System.err.println(e.getMessage());
        }

        if (thisId != 0) {
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, changeBeschrijving);
                preparedStatement.setInt(2, thisId);
                preparedStatement.executeUpdate();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        searchBar.setText("");
        updateTable(actionEvent);
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
        adVersie.setCellValueFactory(new PropertyValueFactory<apparaatObj, String>("hardware"));
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
        adGuiApplication gui = new adGuiApplication();
        makeTableCells();
        list = getAdDeviceData();
        adTafel.setItems(list);
        ppmFiller(); //START BACKGROUND TASK FOR FILLING THE PPM TABLEVIEW
        currentStyle = gui.getStyle();
    }

    private boolean initTrue = false;
    private Stage stage3;

    public void addNewDevice() throws IOException {
        if (stage3 == null) {
            stage3 = new Stage();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addDevice.fxml"));
        Parent root = loader.load();
        addDeviceController controller = loader.getController();
        controller.setAdminController(this);
        Scene scene = new Scene(root, 450, 350);
        scene.getStylesheets().add(getClass().getResource(currentStyle).toExternalForm());
        stage3.setTitle("Add New Device");
        stage3.getIcons().add(new Image(getClass().getResourceAsStream("images/ADLogo_64px.png")));
        stage3.setScene(scene);
        stage3.setResizable(false);
        if (!initTrue) {
            stage3.initModality(Modality.APPLICATION_MODAL);
            stage3.initOwner(adGuiApplication.getScene().getWindow());
            initTrue = true;
        }
        stage3.show();
    }


    void closeAddDevice() {
        if (stage3 != null && stage3.isShowing()) {
            stage3.close();
        } else {
            System.out.println("Stage is already closed or NULL!");
        }
    }

    public Stage getStage3() {
        return stage3;
    }

    public void lightMode() {
        adGuiApplication gui = new adGuiApplication();
        currentStyle = gui.changeStyle(1);
        adGuiApplication.getScene().getStylesheets().clear();
        adGuiApplication.getScene().getStylesheets().add(getClass().getResource(currentStyle).toExternalForm());
    }

    public void darkMode() {
        adGuiApplication gui = new adGuiApplication();
        currentStyle = gui.changeStyle(2);
        adGuiApplication.getScene().getStylesheets().clear();
        adGuiApplication.getScene().getStylesheets().add(getClass().getResource(currentStyle).toExternalForm());
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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    public void resetPpm(ActionEvent actionEvent) {
        adGuiController guiController = new adGuiController();
        guiController.deletePpmSql();
    }


    //      SQL INJECTION ADD GPS ROW
    public void sqlAddGps(int gpsId) {
        String sql = "INSERT INTO gps(gpsId) values (?)";
        Connection conn = connect();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, gpsId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //      SQL INJECT NEW ROW INTO DEVICE
    public void sqlAddDevice(String hardware, String plaats, String beschrijving, boolean gps, int gpsId) {
        String sql = "INSERT INTO addevice (deviceversie, installdatum, plaatsnaam, locatiebeschrijving, gpsBoolean, gpsId) values(?, ?, ?, ?, ?, ?)";
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        Connection conn = connect();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, hardware);
            preparedStatement.setDate(2, sqlDate);
            preparedStatement.setString(3, plaats);
            preparedStatement.setString(4, beschrijving);
            preparedStatement.setBoolean(5, gps);
            if (gpsId != 0) {
                preparedStatement.setInt(6, gpsId);
            } else {
                preparedStatement.setString(6, null);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            conn.close();
        }   catch (SQLException e) {
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
                String adVersie = rs.getString("deviceVersie");
                String installDate = rs.getString("installDatum");
                String plaatsnaam = rs.getString("Plaatsnaam");
                String beschrijving = rs.getString("Locatiebeschrijving");
                double ppm = getLastPpm(id);
                boolean gps = rs.getBoolean("gpsBoolean");
                int gpsId = rs.getInt("gpsId");
                data.add(new apparaatObj(id, adVersie, installDate, plaatsnaam, beschrijving, ppm, gps, gpsId));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return data;
    }

    public double getLastPpm(int id) {
        double lastPpm = 0.0;
        Connection conn = connect();
        if (conn == null) {
            System.err.println("Failed to establish a database connection.");
            return lastPpm;
        }
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT ppmWaarde FROM ppmdata where adid = " + id + " order by updatetijdstip desc limit 1")) {
            while (rs.next()) {
                lastPpm = rs.getDouble("ppmWaarde");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage() + "   [ GEEN PPM VANUIT DATABASE, DEFAULT IS 0.0 ]");
        }

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return lastPpm;
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

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
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
             ResultSet rs = statement.executeQuery("SELECT updateTijdStip, ppmWaarde FROM ppmdata where adid = " + id + " order by updatetijdstip desc limit 40")) {
            while (rs.next()) {
                String ppmDate = rs.getString("updateTijdStip");
                float ppmwaarde = rs.getFloat("ppmWaarde");
                dataPoints.add(new ppmObj(ppmDate, ppmwaarde));
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return dataPoints;
    }

    public double getPpmAvg(int id) {
        double avg = 0.0;
        String sql = "select avg(ppmwaarde) as ppmAvg from ppmdata where adid = ?";

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, id); // Zet de parameterwaarde (adid) in de query

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    avg = rs.getDouble("ppmAvg"); // Haal het gemiddelde op
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            return 0.0;
        }

        return avg;
    }

    //      PPM TABLE FILLER WITH A SELECT SQL INJECTION FROM THE DATABASE
    public void ppmFiller() {

        SwingWorker<Void, Void> ppmWorker = new SwingWorker<>() {

            @Override
            protected  Void doInBackground() {
                int lastId = 0;
                int listLength = 0;

                runPpm = true;

                try {

                    while(runPpm) {
                        apparaatObj currentRow = adTafel.getSelectionModel().getSelectedItem();

                        if(currentRow != null) {
                            int id = currentRow.getAdID(); // set identifiable number to the current row's ID


                            if (id != lastId) {
                                ppmChart.setAnimated(true);
                                lastId = id;
                            }


                            ppmList = getPpm(id);   //      PUT AQUADATA OBJECT LIST INTO A NEW LIST


                            SwingUtilities.invokeLater(() -> {
                                makePpmTableCells(); //make cells for ppm table
                                ppmTafel.setItems(ppmList); //add list of items to the table
                            });
                            if (listLength != ppmList.size()) { //updating chart when length in size changes / ppm is updated
                                new Thread(() -> {
                                    List<ppmObj> chartList = fetchPpmList(id); //fetch the sql data list from the loop AKA fetchppmlist function
                                    Platform.runLater(() -> {
                                        XYChart.Series<String, Float> series1 = new XYChart.Series<>();
                                        int getal = 1; // counter for inserting the amount of sql retrieved data rows/sets

                                        ppmChart.getData().clear(); //clear chart before making a new one
                                        series1.setName("Device Number: " + id);

                                        for (ppmObj object : chartList) {
                                            String nummer = Integer.toString(getal);
                                            series1.getData().add(new XYChart.Data<>(nummer, object.getPpmWaarde()));
                                            getal++;
                                        }
                                        ppmChart.getData().add(series1); // fill the chart

                                        if (!chartList.isEmpty()) {
                                            double lastPpm = Math.abs(chartList.getFirst().getPpmWaarde());
                                            String formattedPpm = String.format("%.2f", lastPpm);
                                            currentRow.setStofWaarde(Double.parseDouble(formattedPpm));
                                            adTafel.refresh();
                                        }

                                        try {
                                            double ppmAverage;
                                            double ppmAve;
                                            ppmAve = getPpmAvg(id);

                                            String str = "";
                                            if (ppmAve < 0.01) {
                                                str = "none";
                                                avgPpm.setStyle("-fx-text-fill:#204000;");
                                                avgPpm.setText(str);
                                            }
                                            else {
                                                str = String.format("%.2f", ppmAve);
                                                ppmAverage = Double.parseDouble(str);
                                                setPpmColor(ppmAverage);
                                                avgPpm.setText(str);
                                            }
                                        } catch (Exception e) {
                                            throw e;
                                        }

                                        ppmChart.setAnimated(false);
                                    });
                                }) .start();

                                listLength = ppmList.size();
                                //Thread.sleep(1500);
                            }
                        }
                        Thread.sleep(500);
                    }
                }
                catch (Exception e) {
                    System.out.println("ERROR: 99 ");
                    e.printStackTrace(); // Dit drukt de foutmeldingen af.
                }
                return null;
            }
        };
        ppmWorker.execute();
    }
    public void setPpmColor(double nb) {
        if (nb > 1000) {
            avgPpm.setStyle("-fx-text-fill: #FF0000;");
        } else if (nb > 875) {
            avgPpm.setStyle("-fx-text-fill: #FF2000;");
        } else if (nb > 750) {
            avgPpm.setStyle("-fx-text-fill: #FF4000;");
        } else if (nb > 650) {
            avgPpm.setStyle("-fx-text-fill: #FF6000;");
        } else if (nb > 550) {
            avgPpm.setStyle("-fx-text-fill: #FF8000;");
        } else if (nb > 475) {
            avgPpm.setStyle("-fx-text-fill: #FFa000;");
        } else if (nb > 400) {
            avgPpm.setStyle("-fx-text-fill: #FFc000;");
        } else if (nb > 350) {
            avgPpm.setStyle("-fx-text-fill: #FFe000;");
        } else if (nb > 300) {
            avgPpm.setStyle("-fx-text-fill: #FFFF00;");
        } else if (nb > 250) {
            avgPpm.setStyle("-fx-text-fill: #e0ff00;");
        } else if (nb > 200) {
            avgPpm.setStyle("-fx-text-fill: #c0ff00;");
        } else if (nb > 150) {
            avgPpm.setStyle("-fx-text-fill: #a0ff00;");
        } else if (nb > 100) {
            avgPpm.setStyle("-fx-text-fill: #80ff00;");
        } else if (nb > 50) {
            avgPpm.setStyle("-fx-text-fill: #60ff00;");
        } else if (nb > 25) {
            avgPpm.setStyle("-fx-text-fill: #40ff00;");
        } else if (nb > 10) {
            avgPpm.setStyle("-fx-text-fill: #20ff00;");
        } else
            avgPpm.setStyle("-fx-text-fill: #00ff00;");

    }

    public void updateTable(ActionEvent actionEvent) {
        list = getAdDeviceData();
        adTafel.setItems(list);
    }
}