package com.example.fraud_detection.dto;

public class FraudDetectionResult {
    private Long id;
    private boolean isFraud;
    private String fraudType;
    private int fraudScore;
    private String riskLevel;
    private String fraudReason;
    private String recommendation;
    private String status;

    // NEW FIELDS FOR PROPER STATUS
    private String approvalStatus;         // APPROVED, PENDING_REVIEW, BLOCKED
    private Boolean transactionApproved;   // true/false/null

    public FraudDetectionResult() {}

    public FraudDetectionResult(Long id, boolean isFraud, String fraudType,
                                int fraudScore, String riskLevel,
                                String fraudReason, String recommendation, String status) {
        this.id = id;
        this.isFraud = isFraud;
        this.fraudType = fraudType;
        this.fraudScore = fraudScore;
        this.riskLevel = riskLevel;
        this.fraudReason = fraudReason;
        this.recommendation = recommendation;
        this.status = status;
        setApprovalStatusFromRiskLevel(riskLevel);
    }

    // ===== Helper method to set approval status based on risk level =====
    public void setApprovalStatusFromRiskLevel(String riskLevel) {
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

    // ===== Getters and Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isFraud() { return isFraud; }
    public void setFraud(boolean fraud) { isFraud = fraud; }

    public String getFraudType() { return fraudType; }
    public void setFraudType(String fraudType) { this.fraudType = fraudType; }

    public int getFraudScore() { return fraudScore; }
    public void setFraudScore(int fraudScore) { this.fraudScore = fraudScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        setApprovalStatusFromRiskLevel(riskLevel);
    }

    public String getFraudReason() { return fraudReason; }
    public void setFraudReason(String fraudReason) { this.fraudReason = fraudReason; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public Boolean getTransactionApproved() { return transactionApproved; }
    public void setTransactionApproved(Boolean transactionApproved) { this.transactionApproved = transactionApproved; }
}