package com.example.fraud_detection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= FRAUD EVALUATION =================
    private Integer fraudScore;
    private String riskLevel;      // LOW, MEDIUM, HIGH
    private String fraudType;

    // ================= TRANSACTION DETAILS =================
    private String accountNumber;
    private Double amount;
    private String currency;
    private String transactionType;
    private String merchantId;
    private String deviceId;
    private String ipAddress;

    // ================= LOCATION DETAILS =================
    private String location;
    private String country;
    private String city;

    // ================= TIMING DETAILS =================
    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;
    private Boolean isNightTime;

    // ================= BEHAVIORAL DETAILS =================
    private Boolean successStatus;
    private Integer transactionCountLastHour;
    private Double averageTransactionAmount;
    private Boolean isUnusualLocation;

    // ================= FRAUD RESULT =================
    private Boolean isFraud;
    private String fraudReason;

    // ================= USER DETAILS =================
    private Integer userId;
    private String userBehaviorScore;

    // ================= APPROVAL STATUS =================
    // APPROVED | PENDING | BLOCKED
    private String approvalStatus;

    // true = approved, false = blocked, null = pending
    private Boolean transactionApproved;

    // =====================================================
    // LIFECYCLE CALLBACKS
    // =====================================================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (transactionTime == null) {
            transactionTime = now;
        }

        if (fraudScore == null) {
            fraudScore = 0;
        }

        if (riskLevel == null) {
            riskLevel = "LOW";
        }

        if (isFraud == null) {
            isFraud = false;
        }

        if (fraudReason == null) {
            fraudReason = "Transaction appears normal";
        }

        isNightTime = transactionTime.getHour() >= 22 || transactionTime.getHour() <= 6;

        // FINAL authoritative approval mapping
        applyApprovalFromRisk();
    }

    @PreUpdate
    protected void onUpdate() {
        applyApprovalFromRisk();
    }

    // =====================================================
    // CORE BUSINESS LOGIC
    // =====================================================

    private void applyApprovalFromRisk() {
        if (riskLevel == null) {
            riskLevel = "LOW";
        }

        switch (riskLevel) {
            case "LOW":
                approvalStatus = "APPROVED";
                transactionApproved = true;
                isFraud = false;
                break;

            case "MEDIUM":
                approvalStatus = "PENDING";
                transactionApproved = null;
                isFraud = true;
                break;

            case "HIGH":
                approvalStatus = "BLOCKED";
                transactionApproved = false;
                isFraud = true;
                break;

            default:
                approvalStatus = "PENDING";
                transactionApproved = null;
                isFraud = true;
        }
    }

    // =====================================================
    // GETTERS & SETTERS
    // =====================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getFraudScore() { return fraudScore; }
    public void setFraudScore(Integer fraudScore) { this.fraudScore = fraudScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        applyApprovalFromRisk();
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
    public void setTransactionTime(LocalDateTime transactionTime) { this.transactionTime = transactionTime; }

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
    public void setIsFraud(Boolean isFraud) { this.isFraud = isFraud; }

    public String getFraudReason() { return fraudReason; }
    public void setFraudReason(String fraudReason) { this.fraudReason = fraudReason; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserBehaviorScore() { return userBehaviorScore; }
    public void setUserBehaviorScore(String userBehaviorScore) {
        this.userBehaviorScore = userBehaviorScore;
    }

    public String getApprovalStatus() { return approvalStatus; }
    public Boolean getTransactionApproved() { return transactionApproved; }
}
