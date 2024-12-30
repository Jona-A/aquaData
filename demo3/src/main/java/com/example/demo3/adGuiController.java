package com.example.demo3;
import com.serialpundit.serial.SerialComManager;
import com.serialpundit.serial.SerialComManager.BAUDRATE;
import com.serialpundit.serial.SerialComManager.DATABITS;
import com.serialpundit.serial.SerialComManager.FLOWCONTROL;
import com.serialpundit.serial.SerialComManager.PARITY;
import com.serialpundit.serial.SerialComManager.STOPBITS;
import com.sun.jdi.connect.Connector;
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
    @FXML Label avgPpm;
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

    public void deletePpmSql() {
        Connection conn = connect();
        String deleteQuery = "delete from ppmdata where adid = ?";

        if (conn == null) {
            System.err.println("Failed to establish a database connection.");
        }
        try (PreparedStatement statement = conn.prepareStatement(deleteQuery)) {
             statement.setInt(1, adDeviceNR);
             statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
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
            while (rs.next()) { lastPpm = rs.getDouble("ppmWaarde"); }
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

        try {
            conn.close();
        }   catch (SQLException e) {
            System.err.println(e.getMessage());
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

    private boolean isValidDouble(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
                    int countPrint = 1;

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
                            try {
                                // String naar float omzetten
                                if (isValidDouble(dataOntvangen)) {
                                    if (countPrint % 10 == 0) {
                                        System.out.println("%" + countPrint + ".");
                                    } else {
                                        System.out.print("%" + countPrint + ", ");
                                    }
                                    countPrint++;
                                    double ppmWaarde = Double.parseDouble(dataOntvangen);
                                    database.insert(tijdstip, adDeviceNR, ppmWaarde);//Deze regel uitcommenten als SQL nog niet werkt.
                                }
                                else {
                                    System.err.println("\nERROR: " + dataOntvangen + " IS NOT A DOUBLE.\nDATA IS BUFFERED.");
                                }
                            }catch (Exception e) {
                                System.err.println("Error-TryBlock COMDATA: " + dataOntvangen);
                                e.printStackTrace();
                            }
                        }
                        Thread.sleep(2090);
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