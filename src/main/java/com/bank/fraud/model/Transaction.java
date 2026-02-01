package com.bank.fraud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(name = "idx_account_id", columnList = "accountId"),
                @Index(name = "idx_transaction_time", columnList = "transactionTime"),
                @Index(name = "idx_status", columnList = "status")
        }
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String transactionId;

    @Column(nullable = false, length = 20)
    private String accountId;

    @Column(nullable = false, length = 50)
    private String customerName;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 30)
    private String transactionType;   // UPI, CARD, NET_BANKING

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false, length = 50)
    private String deviceId;

    @Column(nullable = false)
    private Integer ruleScore;          // 0–100

    @Column(nullable = false)
    private Integer mlScore;            // 0–100

    @Column(nullable = false)
    private Integer finalRiskScore;     // 0–100

    @Column(nullable = false, length = 20)
    private String riskLevel;           // LOW, MEDIUM, HIGH

    @Column(nullable = false, length = 20)
    private String status;              // SUCCESS, FAILED, BLOCKED

    @Column(nullable = false)
    private Boolean fraudDetected;

    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @Column(nullable = false)
    private Long processingTimeMs;

    // ---------- Constructors ----------

    public Transaction() {
        // Required by JPA
    }

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
}
