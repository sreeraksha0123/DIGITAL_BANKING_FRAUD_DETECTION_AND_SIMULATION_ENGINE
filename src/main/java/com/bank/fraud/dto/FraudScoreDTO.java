package com.bank.fraud.dto;

import java.util.List;

public class FraudScoreDTO {

    private Integer ruleScore;          // 0–100
    private Integer mlScore;            // 0–100
    private Integer finalRiskScore;     // 0–100

    private String riskLevel;           // LOW, MEDIUM, HIGH
    private Boolean fraudDetected;

    private List<String> ruleTriggers;  // Explainable reasons

    // ---------- Constructors ----------

    public FraudScoreDTO() {
    }

    // ---------- Getters & Setters ----------

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

    public Boolean getFraudDetected() {
        return fraudDetected;
    }

    public void setFraudDetected(Boolean fraudDetected) {
        this.fraudDetected = fraudDetected;
    }

    public List<String> getRuleTriggers() {
        return ruleTriggers;
    }

    public void setRuleTriggers(List<String> ruleTriggers) {
        this.ruleTriggers = ruleTriggers;
    }
}
