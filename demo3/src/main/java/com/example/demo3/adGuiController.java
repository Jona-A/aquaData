package com.example.demo3;
import com.serialpundit.serial.SerialComManager;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.URL;
import java.util.Date;


public class adGuiController implements Initializable {

    public Text homeLabel;
    private String teVerzenden;
    private int adDeviceNR = 1;
    public boolean runPpm = false;
    public boolean runSql = false;
    String currentStyle;

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
    private Button adminLogin;//adminButton
    @FXML
    public TextField searchBar;
    @FXML
    public LineChart ppmChart;
    // TABLEVIEW LISTS;
    @FXML
    ObservableList<apparaatObj> list = FXCollections.observableArrayList();
    @FXML
    ObservableList<ppmObj> ppmList = FXCollections.observableArrayList();

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

    //     STARTUP INITIALIZATION ~~~~~~~~~~~~
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        adGuiApplication guiApplication = new adGuiApplication();
        currentStyle = guiApplication.getStyle();
        makeTableCells();
        list = getAdDeviceData();
        adTafel.setItems(list);
        ppmFiller(); //START BACKGROUND TASK FOR FILLING THE PPM TABLEVIEW
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

    private boolean initTrue = false;
    private Stage stage2;

    //      SWITCH PAGE INTO LOGIN WINDOW (CREATE A FLOATING ONE AT SOME POINT)
    @FXML
    public void logInPage() throws IOException {
        if (stage2 == null) {
            stage2 = new Stage();
        }
        //Parent root = FXMLLoader.load(getClass().getResource("logIn.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("logIn.fxml"));
        Parent root = loader.load();
        logInController controller = loader.getController();
        controller.setAdGuiController(this);
        Scene scene = new Scene(root, 400, 200);
        scene.getStylesheets().add(getClass().getResource(currentStyle).toExternalForm());
        stage2.setTitle("Log-In page");
        stage2.getIcons().add(new Image(getClass().getResourceAsStream("images/ADLogo_64px.png")));
        stage2.setScene(scene);
        stage2.setResizable(false);
        if (!initTrue) {
            stage2.initModality(Modality.APPLICATION_MODAL);
            stage2.initOwner(adGuiApplication.getScene().getWindow());
            initTrue = true;
        }
        stage2.show();
    }

    //close page after 3 attempts.
    void closeLogIn() {
        if (stage2 != null && stage2.isShowing()) {
            stage2.close();
        } else {
            System.out.println("Stage is already closed or NULL!");
        }
    }

    //find stage2 options from another controller
    public Stage getStage2() {
        return stage2;
    }

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

    //      SQL CONNECTOR FOR GUI-CONTROLLER
    private Connection connect() {
        Connection conn = null;
        String driver = "com.mysql.cj.jdbc.Driver";
        String connection = "jdbc:mysql://localhost:3306/aquadatabase?serverTimezone=UTC";
        String user = "aquaAdmin";
        String password = "geheim";
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(connection, user, password);
        }
        catch (Exception e) {
            System.out.println("SQL CONNECTION FAILED!");
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
        return data;
    }

    public double getLastPpm(int id) {
        double lastPpm = 420.6969;
        Connection conn = connect();
        if (conn == null) {
            System.err.println("Failed to establish a database connection.");
            return lastPpm;
        }
        try (Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT ppmWaarde FROM ppmdata where adid = " + id + " order by updatetijdstip desc limit 1")) {
            lastPpm = rs.getDouble("ppmWaarde");
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage() + "   [ GEEN PPM VANUIT DATABASE, DEFAULT IS 0.0 ]");
        }
        return lastPpm;
    }

    //      FILL A LIST WITH DEVICE OBJECTS TO INSERT INTO TABLEVIEW FROM THE DATABASE
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

    // FUNCTION TO fetch PPM data from SQL table, insert values into a list and return the list
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
                            int id = currentRow.getAdID(); // set identifiable number to the current row's ID

                            ppmList = getPpm(id);   //      PUT AQUADATA OBJECT LIST INTO A NEW LIST
                            SwingUtilities.invokeLater(() -> {
                                makePpmTableCells(); //make cells for ppm table
                                ppmTafel.setItems(ppmList); //add list of items to the table
                            });
                            if (lastId != id) {
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
                                    });
                                }).start();
                                Thread.sleep(100);
                            }
                            lastId = id;
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

    //      SQLRUNNER ~~~ INSERT FROM AQUADATA DEVICE
    public void startSqlTask() {
        runSql = true;
        SwingWorker<Void, String> sqlWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                aquadataSql database = new aquadataSql();   //Deze regel uitcommenten als SQL nog niet werkt.
                String port = "";
                try {
                    SerialComManager scm = new SerialComManager();

                    // Blok hieronder: automatisch de poort met de Microbit kiezen (werkt alleen voor Microbit).
                    String[] poorten = scm.listAvailableComPorts();
                    for (String poort : poorten) {
                        port = poort;
                        System.out.println("Poort " + port + " gekozen..."); // beschikbare poorten afdrukken
                    }
                    if (port.isEmpty()) {
                        System.out.print("EMPTY PORT: ");
                    }
                    // COM poort kun je ook hard invullen, zoek via Arduino of Device Manager uit welke COM poort je gebruikt:
                    // long handle = scm.openComPort("COM3", true, true, true);

                    long handle = scm.openComPort(port, true, true, true);
                    scm.configureComPortData(handle, DATABITS.DB_8, STOPBITS.SB_1, PARITY.P_NONE, BAUDRATE.B9600, 0);
                    scm.configureComPortControl(handle, FLOWCONTROL.NONE, 'x', 'x', false, false);
                    scm.writeString(handle, "test", 0);
                    while (runSql) { // gewoon altijd doorgaan, vergelijkbaar met de Arduino loop()

                        // tijdstip = nu, dit moment.
                        String tijdstip = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        tijdstip = tijdstip.replaceAll("[\\n\\r]", ""); // tijdstip om af te drukken, handig...
                        // Data verzenden via serieel
                        if (teVerzenden != null) {
                            scm.writeString(handle, teVerzenden, 0);
                            System.out.println(tijdstip + " Data verzonden: " + teVerzenden);
                            teVerzenden = null;
                        }
                        // Data ontvangen via serieel
                        String dataOntvangen = scm.readString(handle);
                        if (dataOntvangen != null) { // Er is data ontvangen
                            // verwijder alle newlines '\n' en carriage_returns '\r':
                            dataOntvangen = dataOntvangen.replaceAll("[\\n\\r]", "");
                            System.out.println(tijdstip + " Ontvangen data: " + dataOntvangen);

                            // String naar float omzetten
                            double ppmWaarde = Double.parseDouble(dataOntvangen);
                            database.insert(tijdstip, adDeviceNR, ppmWaarde);  //Deze regel uitcommenten als SQL nog niet werkt.
                        }
                        Thread.sleep(1050);
                    }

                } catch (Exception e) { // Stukje foutafhandeling, wordt als het goed is nooit gebruikt
                    System.out.print("\033[1;93m\033[41m"); // Dikke gele tekst in rode achtergrond (ANSI colors Java)
                    System.out.print("NO AQUADATA DEVICE ACTIVE!");
                    System.out.println("\033[0m"); // Tekstkleuren weer resetten naar standaard.
                    //e.printStackTrace(); // Dit drukt de foutmeldingen af.
                }
                return null;
            }
        };
        sqlWorker.execute();
    }
}