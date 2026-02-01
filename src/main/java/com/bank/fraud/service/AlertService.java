package com.bank.fraud.service;

import com.bank.fraud.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class AlertService {

    // In real systems, these would be external integrations
    // (Email service, SMS gateway, Kafka, WebSocket, etc.)

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // --------------------------------------------------
    // FRAUD ALERT (HIGH RISK)
    // --------------------------------------------------

    public void sendFraudAlert(Transaction transaction) {

        // Build alert message
        String alertMessage = buildFraudAlertMessage(transaction);

        // 1️⃣ Send email alert (simulated)
        sendEmailAlert(alertMessage);

        // 2️⃣ Push real-time dashboard alert (simulated)
        sendDashboardNotification(alertMessage);

        // 3️⃣ (Optional) Can be extended to SMS / Kafka / Slack
    }

    // --------------------------------------------------
    // HELPER METHODS
    // --------------------------------------------------

    private String buildFraudAlertMessage(Transaction transaction) {

        return String.format(
                "🚨 FRAUD ALERT 🚨\n" +
                        "Transaction ID: %s\n" +
                        "Account ID: %s\n" +
                        "Amount: ₹%.2f\n" +
                        "Risk Level: %s\n" +
                        "Final Risk Score: %d\n" +
                        "Time: %s\n",
                transaction.getTransactionId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getRiskLevel(),
                transaction.getFinalRiskScore(),
                transaction.getTransactionTime().format(TIME_FORMAT)
        );
    }

    private void sendEmailAlert(String message) {
        // Simulated email sending
        System.out.println("[EMAIL ALERT SENT]");
        System.out.println(message);
    }

    private void sendDashboardNotification(String message) {
        // Simulated WebSocket / notification push
        System.out.println("[DASHBOARD ALERT PUSHED]");
        System.out.println(message);
    }
}
