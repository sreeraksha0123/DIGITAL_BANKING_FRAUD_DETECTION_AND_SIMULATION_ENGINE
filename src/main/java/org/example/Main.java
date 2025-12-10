package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {

    // Database credentials for MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Femii..16"; // change if needed

    public static void main(String[] args) {

        // Generate fake transaction data
        String transactionId = UUID.randomUUID().toString();
        String timestamp = LocalDateTime.now().toString();
        double amount = Math.random() * 10000;
        String currency = Math.random() > 0.5 ? "INR" : "USD";
        String senderAccount = "ACC" + (int)(Math.random() * 1000000);
        String receiverAccount = "ACC" + (int)(Math.random() * 1000000);
        String transactionType = Math.random() > 0.5 ? "UPI" : "CARD";
        String channel = Math.random() > 0.5 ? "MOBILE" : "WEB";
        String deviceId = "DEV-" + (int)(Math.random() * 999999);
        String ipAddress = (int)(Math.random()*255) + "." +
                (int)(Math.random()*255) + "." +
                (int)(Math.random()*255) + "." +
                (int)(Math.random()*255);
        String location = Math.random() > 0.5 ? "Chennai, India" : "New York, USA";
        boolean successStatus = Math.random() > 0.2;  // assume 80% transactions succeed

        // ----- FRAUD / FAKE / SUSPICIOUS ----- //
        boolean isFraud = Math.random() > 0.7; // 30% chance of fraud

        if (isFraud) {
            amount = amount * 5; // artificially inflate amount
            successStatus = false;
            location = "Unknown / Suspicious Zone";
        }

        // Print on console
        System.out.println("=== Random Transaction Generated ===");
        System.out.println("Transaction ID   : " + transactionId);
        System.out.println("Timestamp        : " + timestamp);
        System.out.println("Amount           : " + amount);
        System.out.println("Fraudulent?      : " + isFraud);
        System.out.println("Saved to Database ✔");

        // Insert into database
        saveTransactionToDatabase(transactionId, timestamp, amount, currency,
                senderAccount, receiverAccount, transactionType, channel,
                deviceId, location, ipAddress, successStatus);
    }

    private static void saveTransactionToDatabase(
            String transactionId, String timestamp, double amount, String currency,
            String senderAccount, String receiverAccount, String transactionType,
            String channel, String deviceId, String location,
            String ipAddress, boolean successStatus
    ) {

        String sql = "INSERT INTO transactions (transaction_id, txn_timestamp, amount, currency, sender_account, receiver_account, transaction_type, channel, device_id, location, ip_address, success_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transactionId);
            stmt.setString(2, timestamp);
            stmt.setDouble(3, amount);
            stmt.setString(4, currency);
            stmt.setString(5, senderAccount);
            stmt.setString(6, receiverAccount);
            stmt.setString(7, transactionType);
            stmt.setString(8, channel);
            stmt.setString(9, deviceId);
            stmt.setString(10, location);
            stmt.setString(11, ipAddress);
            stmt.setBoolean(12, successStatus);

            stmt.executeUpdate();
            System.out.println("Database Insert ✔ Success");

        } catch (SQLException e) {
            System.out.println("Database Insert ✖ Failed");
            e.printStackTrace();
        }
    }
}
