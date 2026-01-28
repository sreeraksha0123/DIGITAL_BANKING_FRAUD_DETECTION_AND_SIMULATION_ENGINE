package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.MlPredictionResult;
import com.example.fraud_detection.entity.Transaction;
import org.springframework.stereotype.Service;

/**
 * ML-based fraud signal generation.
 *
 * CRITICAL: This service produces CONTINUOUS risk signals (0-100),
 * NOT binary fraud decisions. ML is advisory only.
 *
 * This simulates a real ML model (e.g., Random Forest, XGBoost)
 * that produces probabilistic outputs, not hard classifications.
 */
@Service
public class MlPredictionService {

    /**
     * Generates a continuous ML risk score (0-100) based on transaction characteristics.
     * Scores should be realistic: most scores 0-25, few outliers 70+
     */
    public MlPredictionResult predict(Transaction transaction) {

        double score = 0;
        StringBuilder explanation = new StringBuilder("Transaction analysis: ");

        // ================================================================
        // FEATURE 1: AMOUNT DEVIATION (0-30 points)
        // ================================================================
        double amountDeviation = evaluateAmountDeviation(
                transaction.getAmount(),
                transaction.getAverageTransactionAmount()
        );
        score += amountDeviation;

        // ================================================================
        // FEATURE 2: LOCATION SHIFT (0-25 points)
        // ================================================================
        double locationShift = evaluateLocationShift(
                transaction.getIsUnusualLocation(),
                transaction.getCountry()
        );
        score += locationShift;

        // ================================================================
        // FEATURE 3: TEMPORAL PATTERN (0-20 points)
        // ================================================================
        double temporalRisk = evaluateTemporalPattern(
                transaction.getIsNightTime()
        );
        score += temporalRisk;

        // ================================================================
        // FEATURE 4: DEVICE BEHAVIOR (0-15 points)
        // Simulated: in real system, would check device fingerprinting
        // ================================================================
        double deviceRisk = evaluateDeviceBehavior();
        score += deviceRisk;

        // ================================================================
        // FEATURE 5: VELOCITY & FREQUENCY (0-15 points)
        // ================================================================
        double velocityRisk = evaluateVelocityBehavior(
                transaction.getTransactionCountLastHour()
        );
        score += velocityRisk;

        // ================================================================
        // FEATURE 6: MERCHANT CATEGORY (0-10 points)
        // ================================================================
        double merchantRisk = evaluateMerchantCategory(
                transaction.getTransactionType()
        );
        score += merchantRisk;

        // ================================================================
        // FEATURE 7: IP GEOLOCATION (0-15 points)
        // Simulated: in real system, would use IP geolocation service
        // ================================================================
        double ipRisk = evaluateIpGeolocation();
        score += ipRisk;

        // Cap score at 100
        score = Math.min(score, 100);
        score = Math.max(score, 0);

        // Build explanation
        explanation.append("Amount deviation: ").append(String.format("%.1f", amountDeviation))
                .append("; Location shift: ").append(String.format("%.1f", locationShift))
                .append("; Temporal: ").append(String.format("%.1f", temporalRisk))
                .append("; Device: ").append(String.format("%.1f", deviceRisk))
                .append("; Velocity: ").append(String.format("%.1f", velocityRisk));

        return new MlPredictionResult((int) score, explanation.toString());
    }

    // ================================================================
    // ML FEATURE EVALUATION METHODS
    // ================================================================

    /**
     * Amount deviation from user's historical average.
     * Bell-curve: normal amounts low risk, 2-3x deviation high risk.
     */
    private double evaluateAmountDeviation(Double currentAmount, Double averageAmount) {
        if (currentAmount == null || averageAmount == null || averageAmount <= 0) {
            return 0;
        }

        double ratio = currentAmount / averageAmount;

        // 3x or more deviation: extreme risk
        if (ratio >= 3.0) {
            return 30;
        }
        // 2-3x deviation: high risk
        else if (ratio >= 2.0) {
            return 20;
        }
        // 1.5-2x deviation: moderate risk
        else if (ratio >= 1.5) {
            return 12;
        }
        // 1.25-1.5x deviation: slight risk
        else if (ratio >= 1.25) {
            return 6;
        }
        // Within normal range: minimal risk
        else if (ratio >= 0.75) {
            return 1;
        }
        // Below average: not really a fraud signal
        else {
            return 0;
        }
    }

    /**
     * Location shift detection.
     * Unusual locations from user's typical patterns.
     */
    private double evaluateLocationShift(Boolean isUnusualLocation, String country) {
        if (country == null) {
            return 0;
        }

        double baseScore = 0;

        // Unusual location flag
        if (Boolean.TRUE.equals(isUnusualLocation)) {
            baseScore += 15; // Strong signal
        }

        // High-risk country adds extra points
        String countryUpper = country.toUpperCase();
        if (countryUpper.contains("RUSSIA") || countryUpper.contains("CHINA") ||
                countryUpper.contains("NIGERIA") || countryUpper.contains("PAKISTAN") ||
                countryUpper.contains("PHILIPPINES")) {
            baseScore += 10;
        }

        return Math.min(baseScore, 25);
    }

    /**
     * Temporal pattern analysis.
     * Night transactions, unusual hours are risk signals.
     */
    private double evaluateTemporalPattern(Boolean isNightTime) {
        if (Boolean.TRUE.equals(isNightTime)) {
            return 20; // Strong night-time signal
        }
        return 0;
    }

    /**
     * Device behavior modeling (simulated).
     * In production: check device fingerprint, historical device patterns.
     */
    private double evaluateDeviceBehavior() {
        // Simulated: 10% of transactions have device anomalies
        if (Math.random() < 0.10) {
            return 15;
        }
        return 0;
    }

    /**
     * Velocity & frequency analysis.
     * Multiple rapid transactions indicate potential fraud.
     */
    private double evaluateVelocityBehavior(Integer transactionCountLastHour) {
        if (transactionCountLastHour == null) {
            return 0;
        }

        if (transactionCountLastHour > 8) {
            return 15; // Extreme velocity
        } else if (transactionCountLastHour > 5) {
            return 10; // High velocity
        } else if (transactionCountLastHour > 3) {
            return 5; // Moderate velocity
        }
        return 0;
    }

    /**
     * Merchant category risk scoring.
     * Different merchant types have different fraud profiles.
     */
    private double evaluateMerchantCategory(String transactionType) {
        if (transactionType == null) {
            return 0;
        }

        switch (transactionType.toUpperCase()) {
            case "INTERNATIONAL":
                return 10; // Higher risk for international
            case "ONLINE":
                return 6; // Moderate risk
            case "TRANSFER":
                return 7;
            case "CARD":
                return 2; // Lower risk
            default:
                return 0;
        }
    }

    /**
     * IP geolocation mismatch (simulated).
     * In production: use IP geolocation API, compare with user's typical countries.
     */
    private double evaluateIpGeolocation() {
        // Simulated: 5% of transactions have IP mismatches
        if (Math.random() < 0.05) {
            return 15;
        }
        return 0;
    }
}