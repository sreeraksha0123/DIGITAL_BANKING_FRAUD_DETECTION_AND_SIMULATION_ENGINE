package com.example.fraud_detection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fraud.rules")
public class FraudRulesConfig {

    // Thresholds
    private double highAmountThreshold = 50000.0;
    private double veryHighAmountThreshold = 100000.0;
    private int maxTransactionsPerHour = 10;
    private double velocityThresholdMultiplier = 3.0;

    // Scores
    private int highAmountScore = 40;
    private int veryHighAmountScore = 60;
    private int withdrawalScore = 15;
    private int failedTransactionScore = 20;
    private int suspiciousLocationScore = 25;
    private int nightTimeScore = 20;
    private int unusualLocationScore = 30;
    private int velocityScore = 35;
    private int deviceMismatchScore = 25;
    private int ipMismatchScore = 20;

    // Risk thresholds
    private int highRiskThreshold = 60;
    private int mediumRiskThreshold = 30;

    // Getters and Setters
    public double getHighAmountThreshold() { return highAmountThreshold; }
    public void setHighAmountThreshold(double highAmountThreshold) {
        this.highAmountThreshold = highAmountThreshold;
    }

    public double getVeryHighAmountThreshold() { return veryHighAmountThreshold; }
    public void setVeryHighAmountThreshold(double veryHighAmountThreshold) {
        this.veryHighAmountThreshold = veryHighAmountThreshold;
    }

    public int getMaxTransactionsPerHour() { return maxTransactionsPerHour; }
    public void setMaxTransactionsPerHour(int maxTransactionsPerHour) {
        this.maxTransactionsPerHour = maxTransactionsPerHour;
    }

    public double getVelocityThresholdMultiplier() { return velocityThresholdMultiplier; }
    public void setVelocityThresholdMultiplier(double velocityThresholdMultiplier) {
        this.velocityThresholdMultiplier = velocityThresholdMultiplier;
    }

    public int getHighAmountScore() { return highAmountScore; }
    public void setHighAmountScore(int highAmountScore) { this.highAmountScore = highAmountScore; }

    public int getVeryHighAmountScore() { return veryHighAmountScore; }
    public void setVeryHighAmountScore(int veryHighAmountScore) {
        this.veryHighAmountScore = veryHighAmountScore;
    }

    public int getWithdrawalScore() { return withdrawalScore; }
    public void setWithdrawalScore(int withdrawalScore) { this.withdrawalScore = withdrawalScore; }

    public int getFailedTransactionScore() { return failedTransactionScore; }
    public void setFailedTransactionScore(int failedTransactionScore) {
        this.failedTransactionScore = failedTransactionScore;
    }

    public int getSuspiciousLocationScore() { return suspiciousLocationScore; }
    public void setSuspiciousLocationScore(int suspiciousLocationScore) {
        this.suspiciousLocationScore = suspiciousLocationScore;
    }

    public int getNightTimeScore() { return nightTimeScore; }
    public void setNightTimeScore(int nightTimeScore) { this.nightTimeScore = nightTimeScore; }

    public int getUnusualLocationScore() { return unusualLocationScore; }
    public void setUnusualLocationScore(int unusualLocationScore) {
        this.unusualLocationScore = unusualLocationScore;
    }

    public int getVelocityScore() { return velocityScore; }
    public void setVelocityScore(int velocityScore) { this.velocityScore = velocityScore; }

    public int getDeviceMismatchScore() { return deviceMismatchScore; }
    public void setDeviceMismatchScore(int deviceMismatchScore) {
        this.deviceMismatchScore = deviceMismatchScore;
    }

    public int getIpMismatchScore() { return ipMismatchScore; }
    public void setIpMismatchScore(int ipMismatchScore) { this.ipMismatchScore = ipMismatchScore; }

    public int getHighRiskThreshold() { return highRiskThreshold; }
    public void setHighRiskThreshold(int highRiskThreshold) {
        this.highRiskThreshold = highRiskThreshold;
    }

    public int getMediumRiskThreshold() { return mediumRiskThreshold; }
    public void setMediumRiskThreshold(int mediumRiskThreshold) {
        this.mediumRiskThreshold = mediumRiskThreshold;
    }
}