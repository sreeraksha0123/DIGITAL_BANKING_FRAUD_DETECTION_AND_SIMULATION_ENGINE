package com.example.fraud_detection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fraud evaluation fields
    private Integer fraudScore;
    private String riskLevel;
    private String fraudType;

    // Transaction details
    private String accountNumber;
    private Double amount;
    private String currency;
    private String transactionType;
    private String merchantId;
    private String deviceId;
    private String ipAddress;

    // Location details
    private String location;
    private String country;
    private String city;

    // Timing details
    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;
    private Boolean isNightTime;

    // Behavioral fields
    private Boolean successStatus;
    private Integer transactionCountLastHour;
    private Double averageTransactionAmount;
    private Boolean isUnusualLocation;

    // Fraud result
    private Boolean isFraud;
    private String fraudReason;

    // User behavior
    private Integer userId;
    private String userBehaviorScore;

    // ===== NEW FIELDS FOR APPROVAL STATUS =====
    private String approvalStatus;         // APPROVED, PENDING_REVIEW, BLOCKED
    private Boolean transactionApproved;   // true=approved, false=blocked, null=pending

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (transactionTime == null) {
            transactionTime = LocalDateTime.now();
        }
        isNightTime = transactionTime.getHour() >= 22 || transactionTime.getHour() <= 6;

        // Set approval status based on risk level
        setApprovalStatusFromRiskLevel();
    }

    @PreUpdate
    protected void onUpdate() {
        // Update approval status based on risk level
        setApprovalStatusFromRiskLevel();
    }

    // ===== Helper method to set approval status based on risk level =====
    public void setApprovalStatusFromRiskLevel() {
        if (riskLevel == null) {
            return;
        }

        switch (riskLevel) {
            case "LOW":
                this.approvalStatus = "APPROVED";
                this.transactionApproved = true;
                break;
            case "MEDIUM":
                this.approvalStatus = "PENDING_REVIEW";
                this.transactionApproved = null;  // null = waiting for review
                break;
            case "HIGH":
                this.approvalStatus = "BLOCKED";
                this.transactionApproved = false;
                break;
            default:
                this.approvalStatus = "UNKNOWN";
                this.transactionApproved = null;
        }
    }

    // ===== Getters & Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getFraudScore() { return fraudScore; }
    public void setFraudScore(Integer fraudScore) { this.fraudScore = fraudScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        setApprovalStatusFromRiskLevel();
    }

    public String getFraudType() { return fraudType; }
    public void setFraudType(String fraudType) { this.fraudType = fraudType; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public LocalDateTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsNightTime() { return isNightTime; }
    public void setIsNightTime(Boolean isNightTime) { this.isNightTime = isNightTime; }

    public Boolean getSuccessStatus() { return successStatus; }
    public void setSuccessStatus(Boolean successStatus) { this.successStatus = successStatus; }

    public Integer getTransactionCountLastHour() { return transactionCountLastHour; }
    public void setTransactionCountLastHour(Integer transactionCountLastHour) {
        this.transactionCountLastHour = transactionCountLastHour;
    }

    public Double getAverageTransactionAmount() { return averageTransactionAmount; }
    public void setAverageTransactionAmount(Double averageTransactionAmount) {
        this.averageTransactionAmount = averageTransactionAmount;
    }

    public Boolean getIsUnusualLocation() { return isUnusualLocation; }
    public void setIsUnusualLocation(Boolean isUnusualLocation) {
        this.isUnusualLocation = isUnusualLocation;
    }

    public Boolean getIsFraud() { return isFraud; }
    public void setIsFraud(Boolean fraud) { isFraud = fraud; }

    public String getFraudReason() { return fraudReason; }
    public void setFraudReason(String fraudReason) { this.fraudReason = fraudReason; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserBehaviorScore() { return userBehaviorScore; }
    public void setUserBehaviorScore(String userBehaviorScore) {
        this.userBehaviorScore = userBehaviorScore;
    }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public Boolean getTransactionApproved() { return transactionApproved; }
    public void setTransactionApproved(Boolean transactionApproved) { this.transactionApproved = transactionApproved; }
}