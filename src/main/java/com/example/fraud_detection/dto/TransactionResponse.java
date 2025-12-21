package com.example.fraud_detection.dto;

public class TransactionResponse {
    private Long id;
    private Boolean isFraud;
    private String fraudType;
    private Integer fraudScore;
    private String riskLevel;
    private String fraudReason;
    private String recommendation;

    // ===== CORRECTED FIELD NAMES =====
    private String analysisStatus;         // "COMPLETED" or "ERROR" - Did API finish analysis?
    private String approvalStatus;         // "SUCCESS", "FAILURE", "PENDING" - Simple approval state
    private String transactionApproval;    // Better naming

    public TransactionResponse() {}

    public TransactionResponse(Long id, Boolean isFraud, String fraudReason) {
        this.id = id;
        this.isFraud = isFraud;
        this.fraudReason = fraudReason;
        this.analysisStatus = "COMPLETED";
    }

    public TransactionResponse(Long id, Boolean isFraud, String fraudType,
                               Integer fraudScore, String riskLevel,
                               String fraudReason, String recommendation) {
        this.id = id;
        this.isFraud = isFraud;
        this.fraudType = fraudType;
        this.fraudScore = fraudScore;
        this.riskLevel = riskLevel;
        this.fraudReason = fraudReason;
        this.recommendation = recommendation;
        this.analysisStatus = "COMPLETED";  // API successfully analyzed
        setApprovalStatusFromRiskLevel(riskLevel);
    }

    // ===== Helper method to set approval status based on risk level =====
    /**
     * Maps risk level to approval status:
     * LOW    → "SUCCESS"  (approve transaction)
     * MEDIUM → "PENDING"  (wait for review)
     * HIGH   → "FAILURE"  (block transaction)
     */
    public void setApprovalStatusFromRiskLevel(String riskLevel) {
        if (riskLevel == null) {
            return;
        }

        switch (riskLevel) {
            case "LOW":
                this.approvalStatus = "SUCCESS";
                this.transactionApproval = "SUCCESS";
                break;
            case "MEDIUM":
                this.approvalStatus = "PENDING";
                this.transactionApproval = "PENDING";
                break;
            case "HIGH":
                this.approvalStatus = "FAILURE";
                this.transactionApproval = "FAILURE";
                break;
            default:
                this.approvalStatus = "PENDING";
                this.transactionApproval = "PENDING";
        }
    }

    // ===== Getters and Setters =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getIsFraud() { return isFraud; }
    public void setIsFraud(Boolean fraud) { isFraud = fraud; }

    public String getFraudType() { return fraudType; }
    public void setFraudType(String fraudType) { this.fraudType = fraudType; }

    public Integer getFraudScore() { return fraudScore; }
    public void setFraudScore(Integer fraudScore) { this.fraudScore = fraudScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
        setApprovalStatusFromRiskLevel(riskLevel);
    }

    public String getFraudReason() { return fraudReason; }
    public void setFraudReason(String fraudReason) { this.fraudReason = fraudReason; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    // ===== FINAL CORRECTED FIELDS =====
    public String getAnalysisStatus() { return analysisStatus; }
    public void setAnalysisStatus(String analysisStatus) { this.analysisStatus = analysisStatus; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getTransactionApproval() { return transactionApproval; }
    public void setTransactionApproval(String transactionApproval) { this.transactionApproval = transactionApproval; }
}