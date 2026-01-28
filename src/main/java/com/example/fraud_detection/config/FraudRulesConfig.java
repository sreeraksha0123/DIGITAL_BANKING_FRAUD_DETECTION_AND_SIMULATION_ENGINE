package com.example.fraud_detection.config;

import org.springframework.stereotype.Component;

/**
 * Central configuration for fraud rule thresholds.
 *
 * All rule-based fraud logic MUST use this class.
 */
@Component
public class FraudRulesConfig {

    // =========================
    // AMOUNT-BASED RULES
    // =========================

    /**
     * Multiplier over average amount to consider high amount
     * Example: amount > avg * 3
     */
    private int highAmountMultiplier = 3;

    /**
     * Score added when high amount rule triggers
     */
    private int highAmountScore = 40;

    // =========================
    // TIME-BASED RULES
    // =========================

    /**
     * Score added for night-time transactions
     */
    private int nightTransactionScore = 20;

    // =========================
    // LOCATION-BASED RULES
    // =========================

    /**
     * Score added for unusual location
     */
    private int unusualLocationScore = 30;

    // =========================
    // RISK THRESHOLDS
    // =========================

    /**
     * Minimum score to classify as HIGH risk
     */
    private int highRiskThreshold = 70;

    // =========================
    // GETTERS (USED BY RULE ENGINE)
    // =========================

    public int getHighAmountMultiplier() {
        return highAmountMultiplier;
    }

    public int getHighAmountScore() {
        return highAmountScore;
    }

    public int getNightTransactionScore() {
        return nightTransactionScore;
    }

    public int getUnusualLocationScore() {
        return unusualLocationScore;
    }

    public int getHighRiskThreshold() {
        return highRiskThreshold;
    }
}
