package com.example.demo3;

import java.sql.*;

public class aquadataSql {

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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    public void insert(String tijdstip, int adId, double ppmWaarde) {
        String sql = "INSERT INTO ppmData VALUES(?,?,?)";
        try {
            PreparedStatement preparedStatement = connect().prepareStatement(sql);
            preparedStatement.setString(1, tijdstip);
            preparedStatement.setInt(2, adId);
            preparedStatement.setDouble(3, ppmWaarde);
            preparedStatement.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
