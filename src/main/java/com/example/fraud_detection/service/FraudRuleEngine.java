package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.FraudDetectionResult;
import com.example.fraud_detection.entity.Transaction;
import org.springframework.stereotype.Service;

/**
 * Rule-based fraud detection engine with continuous scoring.
 *
 * CRITICAL: Scores are continuous (0-100), NOT categorical.
 * This ensures realistic distributions matching actual banking patterns.
 */
@Service
public class FraudRuleEngine {

    public FraudDetectionResult evaluate(Transaction tx) {

        double score = 0;
        StringBuilder reason = new StringBuilder();

        // ================================================================
        // RULE 1: AMOUNT ANALYSIS (0-25 points)
        // ================================================================
        double amountScore = evaluateAmount(tx.getAmount());
        score += amountScore;
        if (amountScore > 0) {
            reason.append("Amount risk: ").append(String.format("%.1f", amountScore)).append("; ");
        }

        // ================================================================
        // RULE 2: TRANSACTION TYPE (0-20 points)
        // ================================================================
        double typeScore = evaluateTransactionType(tx.getTransactionType());
        score += typeScore;
        if (typeScore > 0) {
            reason.append("Type risk: ").append(String.format("%.1f", typeScore)).append("; ");
        }

        // ================================================================
        // RULE 3: LOCATION RISK (0-15 points)
        // ================================================================
        double locationScore = evaluateLocation(tx.getCountry(), tx.getCity());
        score += locationScore;
        if (locationScore > 0) {
            reason.append("Location risk: ").append(String.format("%.1f", locationScore)).append("; ");
        }

        // ================================================================
        // RULE 4: TIMING PATTERNS (0-10 points)
        // ================================================================
        double timingScore = evaluateTiming(tx.getIsNightTime());
        score += timingScore;
        if (timingScore > 0) {
            reason.append("Timing risk: ").append(String.format("%.1f", timingScore)).append("; ");
        }

        // ================================================================
        // RULE 5: TRANSACTION VELOCITY (0-15 points)
        // ================================================================
        double velocityScore = evaluateVelocity(tx.getTransactionCountLastHour());
        score += velocityScore;
        if (velocityScore > 0) {
            reason.append("Velocity risk: ").append(String.format("%.1f", velocityScore)).append("; ");
        }

        // ================================================================
        // RULE 6: LOCATION ANOMALY (0-15 points)
        // ================================================================
        double anomalyScore = evaluateLocationAnomaly(tx.getIsUnusualLocation());
        score += anomalyScore;
        if (anomalyScore > 0) {
            reason.append("Anomaly risk: ").append(String.format("%.1f", anomalyScore)).append("; ");
        }

        // Ensure score stays within 0-100
        score = Math.min(score, 100);
        score = Math.max(score, 0);

        // ================================================================
        // DECISION LOGIC (continuous, not categorical)
        // ================================================================
        if (score >= 60) {
            return FraudDetectionResult.highRisk((int) score, reason.toString(), "RULE_ENGINE");
        } else if (score >= 30) {
            return FraudDetectionResult.mediumRisk((int) score, reason.toString(), "RULE_ENGINE");
        }

        return FraudDetectionResult.legitimate();
    }

    // ================================================================
    // RULE EVALUATION METHODS (continuous scoring)
    // ================================================================

    /**
     * Amount-based risk scoring (0-25 points).
     * Uses bell curve: normal amounts low risk, extreme amounts high risk.
     */
    private double evaluateAmount(Double amount) {
        if (amount == null) return 0;

        if (amount > 200000) {
            return 25; // Extreme
        } else if (amount > 100000) {
            return 20; // Very high
        } else if (amount > 50000) {
            return 15; // High
        } else if (amount > 20000) {
            return 10; // Moderate-high
        } else if (amount > 10000) {
            return 5; // Slight elevation
        } else if (amount > 5000) {
            return 2; // Minimal
        }
        return 0; // Normal range
    }

    /**
     * Transaction type risk scoring (0-20 points).
     * Different transaction types have different fraud profiles.
     */
    private double evaluateTransactionType(String type) {
        if (type == null) return 0;

        switch (type.toUpperCase()) {
            case "INTERNATIONAL":
                return 20; // Highest risk for type
            case "TRANSFER":
                return 12; // Moderate-high risk
            case "ONLINE":
                return 8; // Moderate risk
            case "CARD":
                return 3; // Lower risk but not zero
            default:
                return 0;
        }
    }

    /**
     * Location risk scoring (0-15 points).
     * Certain countries are higher risk based on fraud statistics.
     */
    private double evaluateLocation(String country, String city) {
        if (country == null) return 0;

        String countryUpper = country.toUpperCase();

        // High-risk countries (common fraud sources)
        if (countryUpper.contains("RUSSIA") || countryUpper.contains("CHINA") ||
                countryUpper.contains("NIGERIA") || countryUpper.contains("PAKISTAN") ||
                countryUpper.contains("PHILIPPINES") || countryUpper.contains("INDIA")) {
            return 15;
        }

        // Medium-risk countries
        if (countryUpper.contains("BRAZIL") || countryUpper.contains("TURKEY") ||
                countryUpper.contains("INDONESIA") || countryUpper.contains("THAILAND")) {
            return 10;
        }

        // Low-risk countries (USA, Canada, UK, etc.)
        if (countryUpper.contains("USA") || countryUpper.contains("CANADA") ||
                countryUpper.contains("UK") || countryUpper.contains("UNITED KINGDOM")) {
            return 1;
        }

        // Default: neutral
        return 5;
    }

    /**
     * Timing pattern risk scoring (0-10 points).
     * Night transactions have higher fraud likelihood.
     */
    private double evaluateTiming(Boolean isNightTime) {
        if (Boolean.TRUE.equals(isNightTime)) {
            return 10; // Night transactions are suspicious
        }
        return 0;
    }

    /**
     * Velocity-based risk scoring (0-15 points).
     * Multiple transactions in short time indicates potential attack.
     */
    private double evaluateVelocity(Integer transactionCountLastHour) {
        if (transactionCountLastHour == null) return 0;

        if (transactionCountLastHour > 10) {
            return 15; // Extreme velocity
        } else if (transactionCountLastHour > 7) {
            return 12; // High velocity
        } else if (transactionCountLastHour > 5) {
            return 10; // Moderate-high velocity
        } else if (transactionCountLastHour > 3) {
            return 5; // Slight elevation
        }
        return 0;
    }

    /**
     * Location anomaly risk scoring (0-15 points).
     * Unusual locations (impossible travel, new device location) flag fraud.
     */
    private double evaluateLocationAnomaly(Boolean isUnusualLocation) {
        if (Boolean.TRUE.equals(isUnusualLocation)) {
            return 15; // Strong signal
        }
        return 0;
    }
}