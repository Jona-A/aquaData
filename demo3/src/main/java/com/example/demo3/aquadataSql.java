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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return conn;
    }

    public void insert(String tijdstip, int adId, double ppmWaarde) {
        String sql = "INSERT INTO ppmData VALUES(?,?,?)";
        Connection conn = connect();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, tijdstip);
            preparedStatement.setInt(2, adId);
            preparedStatement.setDouble(3, ppmWaarde);
            preparedStatement.executeUpdate();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    //      SQL INJECT NEW ROW INTO DEVICE      INACTIVE
//    public void sqlAddDevice(int id, String hardware, String plaats, String beschrijving, boolean gps, int gpsId) {
//
//        String sql = "INSERT INTO addevice values(?, ?, ?, ?, ?, ?, ?)";
//        java.util.Date utilDate = new java.util.Date();
//        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
//
//        try {
//            PreparedStatement preparedStatement = connect().prepareStatement(sql);
//            preparedStatement.setInt(1, id);
//            preparedStatement.setString(2, hardware);
//            preparedStatement.setDate(3, sqlDate);
//            preparedStatement.setString(4, plaats);
//            preparedStatement.setString(5, beschrijving);
//            preparedStatement.setBoolean(6, gps);
//            if (gpsId != 0) {
//                preparedStatement.setInt(7, gpsId);
//            }
//            else {
//                String nully = null;
//                preparedStatement.setString(7, nully);
//            }
//            preparedStatement.executeUpdate();
//        }
//        catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//    }
}
