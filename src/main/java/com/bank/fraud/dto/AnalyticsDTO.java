package com.bank.fraud.dto;

public class AnalyticsDTO {

    private Long totalTransactions;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long fraudTransactions;

    private Double fraudRatePercentage;
    private Double averageFraudScore;
    private Double detectionAccuracy;

    private Long lowRiskCount;
    private Long mediumRiskCount;
    private Long highRiskCount;

    // ---------- Constructors ----------

    public AnalyticsDTO() {
    }

    // ---------- Getters & Setters ----------

    public Long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public Long getSuccessfulTransactions() {
        return successfulTransactions;
    }

    public void setSuccessfulTransactions(Long successfulTransactions) {
        this.successfulTransactions = successfulTransactions;
    }

    public Long getFailedTransactions() {
        return failedTransactions;
    }

    public void setFailedTransactions(Long failedTransactions) {
        this.failedTransactions = failedTransactions;
    }

    public Long getFraudTransactions() {
        return fraudTransactions;
    }

    public void setFraudTransactions(Long fraudTransactions) {
        this.fraudTransactions = fraudTransactions;
    }

    public Double getFraudRatePercentage() {
        return fraudRatePercentage;
    }

    public void setFraudRatePercentage(Double fraudRatePercentage) {
        this.fraudRatePercentage = fraudRatePercentage;
    }

    public Double getAverageFraudScore() {
        return averageFraudScore;
    }

    public void setAverageFraudScore(Double averageFraudScore) {
        this.averageFraudScore = averageFraudScore;
    }

    public Double getDetectionAccuracy() {
        return detectionAccuracy;
    }

    public void setDetectionAccuracy(Double detectionAccuracy) {
        this.detectionAccuracy = detectionAccuracy;
    }

    public Long getLowRiskCount() {
        return lowRiskCount;
    }

    public void setLowRiskCount(Long lowRiskCount) {
        this.lowRiskCount = lowRiskCount;
    }

    public Long getMediumRiskCount() {
        return mediumRiskCount;
    }

    public void setMediumRiskCount(Long mediumRiskCount) {
        this.mediumRiskCount = mediumRiskCount;
    }

    public Long getHighRiskCount() {
        return highRiskCount;
    }

    public void setHighRiskCount(Long highRiskCount) {
        this.highRiskCount = highRiskCount;
    }
}
