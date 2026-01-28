package com.example.fraud_detection.dto;

/**
 * FINAL fraud decision produced by the fraud detection pipeline.
 *
 * This object is the SINGLE SOURCE OF TRUTH for:
 *  - fraud score
 *  - risk level
 *  - fraud flag
 *  - fraud reason
 *
 * Once created, it must NOT be modified.
 */
public class FraudDetectionResult {

    private int fraudScore;
    private String riskLevel;
    private boolean fraud;
    private String fraudReason;
    private String fraudType;

    public static FraudDetectionResult legitimate() {
        return new FraudDetectionResult(0, "LOW", false, null, null);
    }

    public static FraudDetectionResult mediumRisk(
            int score, String reason, String type) {
        return new FraudDetectionResult(score, "MEDIUM", true, reason, type);
    }

    public static FraudDetectionResult highRisk(
            int score, String reason, String type) {
        return new FraudDetectionResult(score, "HIGH", true, reason, type);
    }

    private FraudDetectionResult(
            int fraudScore,
            String riskLevel,
            boolean fraud,
            String fraudReason,
            String fraudType
    ) {
        this.fraudScore = fraudScore;
        this.riskLevel = riskLevel;
        this.fraud = fraud;
        this.fraudReason = fraudReason;
        this.fraudType = fraudType;
    }

    public int getFraudScore() { return fraudScore; }
    public String getRiskLevel() { return riskLevel; }
    public boolean isFraud() { return fraud; }
    public String getFraudReason() { return fraudReason; }
    public String getFraudType() { return fraudType; }
}
