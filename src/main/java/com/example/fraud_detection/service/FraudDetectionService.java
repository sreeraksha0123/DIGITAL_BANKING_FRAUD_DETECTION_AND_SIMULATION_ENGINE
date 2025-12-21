package com.example.fraud_detection.service;

import com.example.fraud_detection.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionService {

    public void detectFraud(Transaction transaction) {

        int fraudScore = 0;
        StringBuilder reason = new StringBuilder();

        // Rule 1: High amount
        if (transaction.getAmount() != null && transaction.getAmount() > 50000) {
            fraudScore += 40;
            reason.append("Very high amount; ");
        }

        // Rule 2: Withdrawal is risky
        if ("WITHDRAW".equalsIgnoreCase(transaction.getTransactionType())) {
            fraudScore += 15;
            reason.append("Withdrawal transaction; ");
        }

        // Rule 3: Failed transaction
        if (Boolean.FALSE.equals(transaction.getSuccessStatus())) {
            fraudScore += 20;
            reason.append("Failed transaction; ");
        }

        // Rule 4: Suspicious location
        if (transaction.getLocation() != null &&
                transaction.getLocation().toLowerCase().contains("unknown")) {
            fraudScore += 25;
            reason.append("Suspicious location; ");
        }

        // Set final fraud decision
        transaction.setFraudScore(fraudScore);

        if (fraudScore >= 60) {
            transaction.setIsFraud(true);  // CHANGED: setFraud → setIsFraud
            transaction.setRiskLevel("HIGH");
        } else if (fraudScore >= 30) {
            transaction.setIsFraud(true);  // CHANGED: setFraud → setIsFraud
            transaction.setRiskLevel("MEDIUM");
        } else {
            transaction.setIsFraud(false); // CHANGED: setFraud → setIsFraud
            transaction.setRiskLevel("LOW");
        }

        transaction.setFraudReason(
                reason.length() > 0 ? reason.toString() : "Transaction looks normal"
        );
    }
}