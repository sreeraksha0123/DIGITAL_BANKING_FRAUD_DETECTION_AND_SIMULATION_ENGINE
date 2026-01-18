package com.example.fraud_detection.service;

import com.example.fraud_detection.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailAlertService {

    private static final Logger logger = LoggerFactory.getLogger(EmailAlertService.class);

    public void sendFraudAlert(Transaction transaction) {
        logger.warn("🚨 FRAUD ALERT | TXN={} | ACCOUNT={} | AMOUNT={} | RISK={} | SCORE={}",
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getAmount(),
                transaction.getRiskLevel(),
                transaction.getFraudScore());
    }

    public void sendCustomerAlert(Transaction transaction, String customerEmail) {
        logger.info("📧 CUSTOMER ALERT → Email={} | TXN={} | Risk={}",
                customerEmail,
                transaction.getId(),
                transaction.getRiskLevel());
    }

    public void sendSecurityTeamAlert(Transaction transaction) {
        logger.error("🔴 SECURITY ALERT → BLOCKED TXN | ID={} | Account={} | Score={}",
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getFraudScore());
    }
}
