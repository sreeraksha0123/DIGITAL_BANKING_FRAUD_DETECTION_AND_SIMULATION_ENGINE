package com.bank.fraud.ml;

import com.bank.fraud.dto.TransactionRequestDTO;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ModelFeatureMapper {

    /**
     * Converts raw transaction request data
     * into ML-friendly numerical features.
     */
    public Map<String, Double> mapToFeatures(TransactionRequestDTO request) {

        Map<String, Double> features = new HashMap<>();

        // --------------------------------------------------
        // BASIC NUMERICAL FEATURES
        // --------------------------------------------------

        features.put("amount", request.getAmount());

        // --------------------------------------------------
        // TRANSACTION TYPE (ONE-HOT / ENCODED)
        // --------------------------------------------------

        features.put("is_upi",
                "UPI".equalsIgnoreCase(request.getTransactionType()) ? 1.0 : 0.0);
        features.put("is_card",
                "CARD".equalsIgnoreCase(request.getTransactionType()) ? 1.0 : 0.0);
        features.put("is_wallet",
                "WALLET".equalsIgnoreCase(request.getTransactionType()) ? 1.0 : 0.0);
        features.put("is_net_banking",
                "NET_BANKING".equalsIgnoreCase(request.getTransactionType()) ? 1.0 : 0.0);

        // --------------------------------------------------
        // TIME-BASED FEATURES
        // --------------------------------------------------

        int hour = LocalDateTime.now().getHour();
        features.put("transaction_hour", (double) hour);

        boolean isLateNight = (hour >= 23 || hour <= 4);
        features.put("is_late_night", isLateNight ? 1.0 : 0.0);

        // --------------------------------------------------
        // LOCATION / DEVICE FEATURES (SIMULATED)
        // --------------------------------------------------

        features.put("is_unknown_location",
                "UNKNOWN".equalsIgnoreCase(request.getCity()) ? 1.0 : 0.0);

        features.put("device_present",
                request.getDeviceId() != null ? 1.0 : 0.0);

        // --------------------------------------------------
        // NORMALIZATION / DEFAULT HANDLING
        // --------------------------------------------------

        normalize(features);

        return features;
    }

    /**
     * Simple normalization logic.
     * In real systems, this matches training-time normalization.
     */
    private void normalize(Map<String, Double> features) {

        // Normalize amount (example scaling)
        if (features.containsKey("amount")) {
            double amount = features.get("amount");
            features.put("amount", Math.min(amount / 200000.0, 1.0));
        }
    }
}
