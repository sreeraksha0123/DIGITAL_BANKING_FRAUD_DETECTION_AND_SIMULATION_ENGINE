package com.example.fraud_detection.service;

import com.example.fraud_detection.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailAlertService {

    private static final Logger logger = LoggerFactory.getLogger(EmailAlertService.class);

    public void sendFraudAlert(Transaction transaction) {
        logger.warn("🚨 [FRAUD ALERT] Transaction ID: {}, Account: {}, Amount: {}, Risk Level: {}, Fraud Score: {}",
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getAmount(),
                transaction.getRiskLevel(),
                transaction.getFraudScore());

        // In production, integrate with email service (SendGrid, AWS SES, etc.)
        // EmailClient.send(
        //     to: "security@bank.com",
        //     subject: "Fraud Alert",
        //     body: "Transaction " + transaction.getId() + " flagged as " + transaction.getRiskLevel()
        // );
    }

    public void sendCustomerAlert(Transaction transaction, String customerEmail) {
        logger.info("📧 [CUSTOMER ALERT] Suspicious activity detected. Email: {}, Transaction ID: {}",
                customerEmail, transaction.getId());

        // In production, send actual email to customer
    }

    public void sendSecurityTeamAlert(Transaction transaction) {
        logger.warn("⚠️ [SECURITY TEAM ALERT] High risk transaction requires manual review. "
                        + "ID: {}, Account: {}, Score: {}, Type: {}",
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getFraudScore(),
                transaction.getFraudType());
    }
}