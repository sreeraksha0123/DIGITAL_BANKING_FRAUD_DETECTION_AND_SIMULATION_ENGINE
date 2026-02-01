package com.bank.fraud.service;

import com.bank.fraud.dto.TransactionRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class MLFraudScoringService {

    /*
     * NOTE:
     * In a real production system, this service would:
     * - Call a Python ML microservice OR
     * - Load a trained model (PMML / ONNX) OR
     * - Query an ML API
     *
     * For internship/demo purposes, we simulate ML behavior
     * using weighted heuristics + randomness.
     */

    private static final Random RANDOM = new Random();

    // --------------------------------------------------
    // MAIN ML PREDICTION METHOD
    // --------------------------------------------------

    public int predictFraudScore(TransactionRequestDTO request) {

        int score = 0;

        // 1️⃣ Amount-based ML influence
        if (request.getAmount() > 100000) {
            score += 40;
        } else if (request.getAmount() > 50000) {
            score += 25;
        } else if (request.getAmount() > 20000) {
            score += 15;
        }

        // 2️⃣ Transaction type risk
        if ("CARD".equalsIgnoreCase(request.getTransactionType())) {
            score += 15;
        } else if ("WALLET".equalsIgnoreCase(request.getTransactionType())) {
            score += 10;
        }

        // 3️⃣ Time-based behavior
        int hour = LocalDateTime.now().getHour();
        if (hour >= 23 || hour <= 4) {
            score += 15;
        }

        // 4️⃣ Location risk simulation
        if ("UNKNOWN".equalsIgnoreCase(request.getCity())) {
            score += 20;
        }

        // 5️⃣ Add controlled randomness (simulates model uncertainty)
        score += RANDOM.nextInt(15); // 0–14

        // Cap ML score between 0 and 100
        return Math.min(score, 100);
    }
}
