package com.example.fraud_detection.dto;

public class MlPredictionResult {

    private String prediction; // FRAUD / SAFE / UNKNOWN
    private double probability;

    public MlPredictionResult() {}

    public MlPredictionResult(String prediction, double probability) {
        this.prediction = prediction;
        this.probability = probability;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
