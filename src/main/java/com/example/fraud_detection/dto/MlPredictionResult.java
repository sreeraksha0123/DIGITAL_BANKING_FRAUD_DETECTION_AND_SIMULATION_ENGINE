package com.example.fraud_detection.dto;

/**
 * Output of the ML prediction step.
 *
 * This is a probabilistic signal only.
 * It must NOT decide fraud or approval.
 */
public class MlPredictionResult {

    /**
     * Risk score from 0–100
     */
    private final int riskScore;

    /**
     * Optional explanation of the signal
     */
    private final String explanation;

    public MlPredictionResult(int riskScore, String explanation) {
        this.riskScore = riskScore;
        this.explanation = explanation;
    }

    /**
     * Returns the ML risk score (0–100)
     */
    public int getRiskScore() {
        return riskScore;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return "MlPredictionResult{" +
                "riskScore=" + riskScore +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
