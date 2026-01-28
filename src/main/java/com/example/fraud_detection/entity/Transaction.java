package com.example.fraud_detection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // CORE TRANSACTION DATA
    // =====================================================

    private String accountNumber;
    private Double amount;
    private String currency;
    private String transactionType;
    private String merchantId;
    private String deviceId;
    private String ipAddress;

    private String location;
    private String country;
    private String city;

    private LocalDateTime transactionTime;
    private LocalDateTime createdAt;

    // =====================================================
    // FRAUD DECISION (FINAL, APPLIED ONCE)
    // =====================================================

    private Integer fraudScore;

    /**
     * LOW | MEDIUM | HIGH
     */
    private String riskLevel;

    /**
     * true  -> fraudulent (MEDIUM or HIGH)
     * false -> legitimate
     */
    private Boolean isFraud;

    private String fraudReason;
    private String fraudType;

    // =====================================================
    // APPROVAL OUTCOME (DERIVED FROM RISK LEVEL)
    // =====================================================

    /**
     * APPROVED | PENDING | BLOCKED
     */
    private String approvalStatus;

    /**
     * true  -> approved
     * false -> blocked
     * null  -> pending/manual review
     */
    private Boolean transactionApproved;

    // =====================================================
    // ANALYTICS / BEHAVIORAL SIGNALS (OPTIONAL)
    // =====================================================

    private Boolean successStatus;
    private Boolean isNightTime;
    private Boolean isUnusualLocation;
    private Integer transactionCountLastHour;
    private Double averageTransactionAmount;

    private Integer userId;
    private String userBehaviorScore;

    // =====================================================
    // LIFECYCLE CALLBACKS (NO BUSINESS LOGIC)
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

        // Derived flag ONLY (safe)
        this.isNightTime =
                transactionTime.getHour() >= 22 || transactionTime.getHour() <= 6;
    }

    // =====================================================
    // APPLY FINAL FRAUD DECISION (SINGLE ENTRY POINT)
    // =====================================================

    /**
     * This method MUST be called exactly once
     * after fraud detection is complete.
     */
    public void applyFraudDecision(
            Integer fraudScore,
            String riskLevel,
            Boolean isFraud,
            String fraudReason,
            String fraudType
    ) {
        this.fraudScore = fraudScore;
        this.riskLevel = riskLevel;
        this.isFraud = isFraud;
        this.fraudReason = fraudReason;
        this.fraudType = fraudType;

        applyApprovalFromRisk();
    }

    /**
     * Approval mapping is deterministic and simple.
     * NO fraud logic here.
     */
    private void applyApprovalFromRisk() {
        if (riskLevel == null) {
            this.approvalStatus = "PENDING";
            this.transactionApproved = null;
            return;
        }

        switch (riskLevel) {
            case "LOW":
                this.approvalStatus = "APPROVED";
                this.transactionApproved = true;
                break;

            case "MEDIUM":
                this.approvalStatus = "PENDING";
                this.transactionApproved = null;
                break;

            case "HIGH":
                this.approvalStatus = "BLOCKED";
                this.transactionApproved = false;
                break;

            default:
                this.approvalStatus = "PENDING";
                this.transactionApproved = null;
        }
    }

    // =====================================================
    // GETTERS & SETTERS (NO SIDE EFFECTS)
    // =====================================================

    public Long getId() { return id; }

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

    public Integer getFraudScore() { return fraudScore; }
    public String getRiskLevel() { return riskLevel; }
    public Boolean getIsFraud() { return isFraud; }
    public String getFraudReason() { return fraudReason; }
    public String getFraudType() { return fraudType; }

    public String getApprovalStatus() { return approvalStatus; }
    public Boolean getTransactionApproved() { return transactionApproved; }

    public Boolean getSuccessStatus() { return successStatus; }
    public void setSuccessStatus(Boolean successStatus) { this.successStatus = successStatus; }

    public Boolean getIsNightTime() { return isNightTime; }

    public Boolean getIsUnusualLocation() { return isUnusualLocation; }
    public void setIsUnusualLocation(Boolean isUnusualLocation) { this.isUnusualLocation = isUnusualLocation; }

    public Integer getTransactionCountLastHour() { return transactionCountLastHour; }
    public void setTransactionCountLastHour(Integer transactionCountLastHour) {
        this.transactionCountLastHour = transactionCountLastHour;
    }

    public Double getAverageTransactionAmount() { return averageTransactionAmount; }
    public void setAverageTransactionAmount(Double averageTransactionAmount) {
        this.averageTransactionAmount = averageTransactionAmount;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserBehaviorScore() { return userBehaviorScore; }
    public void setUserBehaviorScore(String userBehaviorScore) {
        this.userBehaviorScore = userBehaviorScore;
    }
}
