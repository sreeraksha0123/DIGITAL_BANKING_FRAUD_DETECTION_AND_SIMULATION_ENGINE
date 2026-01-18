package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.MlPredictionResult;
import com.example.fraud_detection.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MlPredictionService {

    private static final Logger logger = LoggerFactory.getLogger(MlPredictionService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // Python ML service endpoint
    private static final String ML_API_URL = "http://localhost:5000/predict";

    public MlPredictionResult predict(Transaction transaction) {
        try {
            MlPredictionResult result =
                    restTemplate.postForObject(ML_API_URL, transaction, MlPredictionResult.class);

            logger.info("🤖 ML Prediction → {} (probability={})",
                    result.getPrediction(), result.getProbability());

            return result;
        } catch (Exception e) {
            logger.error("ML service unavailable. Falling back to rule-based decision.", e);

            // SAFE fallback (important for demos)
            return new MlPredictionResult("UNKNOWN", 0.0);
        }
    }
}
