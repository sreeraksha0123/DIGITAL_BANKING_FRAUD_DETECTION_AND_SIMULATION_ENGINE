package com.bank.fraud.dto;

import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private String transactionId;
    private String accountId;
    private Double amount;
    private String transactionType;
    private String city;

    private Integer ruleScore;
    private Integer mlScore;
    private Integer finalRiskScore;
    private String riskLevel;

    private String status;          // SUCCESS, FAILED, BLOCKED
    private Boolean fraudDetected;

    private Long processingTimeMs;
    private LocalDateTime transactionTime;

    // ---------- Constructors ----------

    public TransactionResponseDTO() {
    }

    // ---------- Getters & Setters ----------

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getRuleScore() {
        return ruleScore;
    }

    public void setRuleScore(Integer ruleScore) {
        this.ruleScore = ruleScore;
    }

    public Integer getMlScore() {
        return mlScore;
    }

    public void setMlScore(Integer mlScore) {
        this.mlScore = mlScore;
    }

    public Integer getFinalRiskScore() {
        return finalRiskScore;
    }

    public void setFinalRiskScore(Integer finalRiskScore) {
        this.finalRiskScore = finalRiskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(Boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
