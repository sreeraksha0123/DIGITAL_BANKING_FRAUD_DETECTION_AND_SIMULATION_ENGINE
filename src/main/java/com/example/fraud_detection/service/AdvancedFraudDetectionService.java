package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.FraudDetectionResult;
import com.example.fraud_detection.dto.MlPredictionResult;
import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvancedFraudDetectionService {

    private static final Logger logger =
            LoggerFactory.getLogger(AdvancedFraudDetectionService.class);

    private final TransactionRepository transactionRepository;
    private final FraudRuleEngine fraudRuleEngine;
    private final EmailAlertService emailAlertService;
    private final MlPredictionService mlPredictionService;
    private final NotificationService notificationService;

    public AdvancedFraudDetectionService(
            TransactionRepository transactionRepository,
            FraudRuleEngine fraudRuleEngine,
            EmailAlertService emailAlertService,
            MlPredictionService mlPredictionService,
            NotificationService notificationService
    ) {
        this.transactionRepository = transactionRepository;
        this.fraudRuleEngine = fraudRuleEngine;
        this.emailAlertService = emailAlertService;
        this.mlPredictionService = mlPredictionService;
        this.notificationService = notificationService;
    }

    // ================= SINGLE TRANSACTION =================
    @Transactional
    public FraudDetectionResult analyzeTransaction(TransactionRequest request) {

        if (request.getAccountNumber() == null || request.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }

        Transaction transaction = convertToEntity(request);

        int fraudScore = fraudRuleEngine.calculateFraudScore(transaction);
        String riskLevel = fraudRuleEngine.determineRiskLevel(fraudScore);
        String recommendation = fraudRuleEngine.getRecommendation(fraudScore, riskLevel);

        MlPredictionResult mlResult = mlPredictionService.predict(transaction);

        boolean isFraud =
                "HIGH".equals(riskLevel) ||
                        ("MEDIUM".equals(riskLevel)
                                && "FRAUD".equalsIgnoreCase(mlResult.getPrediction())) ||
                        mlResult.getProbability() > 0.8;

        String action;
        if ("HIGH".equals(riskLevel)) {
            action = "BLOCKED";
        } else if ("MEDIUM".equals(riskLevel)) {
            action = "PENDING";
        } else {
            action = "APPROVED";
        }

        transaction.setFraudScore(fraudScore);
        transaction.setRiskLevel(riskLevel);
        transaction.setIsFraud(isFraud);

        Transaction saved = transactionRepository.save(transaction);

        // ================= EMAIL =================
        if ("BLOCKED".equals(action)) {
            emailAlertService.sendSecurityTeamAlert(saved);
        } else if ("PENDING".equals(action)) {
            emailAlertService.sendCustomerAlert(saved, "customer@example.com");
        }

        // ================= NOTIFICATIONS (FIXED) =================
        if ("BLOCKED".equals(action)) {
            notificationService.add(
                    "🚨 Transaction BLOCKED | Account " + saved.getAccountNumber()
            );
        } else if ("PENDING".equals(action)) {
            notificationService.add(
                    "⚠️ Transaction PENDING | Account " + saved.getAccountNumber()
            );
        } else {
            notificationService.add(
                    "✅ Transaction APPROVED | Account " + saved.getAccountNumber()
            );
        }

        logAudit(saved, fraudScore, riskLevel, isFraud, action);

        return new FraudDetectionResult(
                saved.getId(),
                isFraud,
                saved.getFraudType() != null ? saved.getFraudType() : "NONE",
                fraudScore,
                riskLevel,
                saved.getFraudReason() != null
                        ? saved.getFraudReason()
                        : "No issues detected",
                recommendation,
                action
        );
    }

    // ================= API RESPONSE =================
    public TransactionResponse processTransaction(TransactionRequest request) {
        try {
            FraudDetectionResult r = analyzeTransaction(request);
            return new TransactionResponse(
                    r.getId(),
                    r.isFraud(),
                    r.getFraudType(),
                    r.getFraudScore(),
                    r.getRiskLevel(),
                    r.getFraudReason(),
                    r.getRecommendation()
            );
        } catch (Exception e) {
            logger.error("Processing failed", e);
            return new TransactionResponse(
                    null,
                    false,
                    "ERROR",
                    0,
                    "ERROR",
                    e.getMessage(),
                    "Retry later"
            );
        }
    }

    // ================= ENTITY =================
    private Transaction convertToEntity(TransactionRequest r) {
        Transaction t = new Transaction();
        t.setAccountNumber(r.getAccountNumber());
        t.setAmount(r.getAmount());
        t.setCurrency(r.getCurrency() != null ? r.getCurrency() : "USD");
        t.setTransactionType(
                r.getTransactionType() != null
                        ? r.getTransactionType().toUpperCase()
                        : "TRANSFER"
        );
        t.setLocation(r.getLocation());
        t.setCountry(r.getCountry());
        t.setCity(r.getCity());
        t.setTransactionTime(
                r.getTransactionTime() != null
                        ? r.getTransactionTime()
                        : LocalDateTime.now()
        );
        t.setIsFraud(false);
        t.setSuccessStatus(true);
        t.setFraudReason("Analysis pending");
        return t;
    }

    // ================= AUDIT =================
    private void logAudit(
            Transaction t,
            int score,
            String risk,
            boolean fraud,
            String action
    ) {
        logger.info("""
            ===== TRANSACTION AUDIT =====
            ID: {}
            Account: {}
            Amount: {}
            Score: {}
            Risk: {}
            Fraud: {}
            Action: {}
            =============================
            """,
                t.getId(),
                t.getAccountNumber(),
                t.getAmount(),
                score,
                risk,
                fraud,
                action
        );
    }
}