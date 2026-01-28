package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.FraudDetectionResult;
import com.example.fraud_detection.entity.Transaction;
import org.springframework.stereotype.Service;

/**
 * Scenario-based fraud detection.
 *
 * Scenarios represent known fraud patterns
 * and override rule-based and ML signals.
 */
@Service
public class FraudScenarioService {

    /**
     * Evaluates predefined fraud scenarios.
     *
     * @return FraudDetectionResult if scenario matches, else null
     */
    public FraudDetectionResult evaluate(Transaction transaction) {

        // Scenario 1: High velocity + high amount
        if (transaction.getTransactionCountLastHour() != null &&
                transaction.getTransactionCountLastHour() > 15 &&
                transaction.getAmount() != null &&
                transaction.getAmount() > 50000) {

            return FraudDetectionResult.highRisk(
                    90,
                    "High transaction velocity combined with large amount",
                    "VELOCITY_ATTACK"
            );
        }

        // Scenario 2: Foreign country + unusual location
        if (Boolean.TRUE.equals(transaction.getIsUnusualLocation()) &&
                transaction.getCountry() != null &&
                !"India".equalsIgnoreCase(transaction.getCountry())) {

            return FraudDetectionResult.highRisk(
                    85,
                    "Transaction from unusual foreign location",
                    "FOREIGN_LOCATION"
            );
        }

        // Scenario 3: Repeated failures (optional)
        if (Boolean.FALSE.equals(transaction.getSuccessStatus()) &&
                transaction.getTransactionCountLastHour() != null &&
                transaction.getTransactionCountLastHour() > 10) {

            return FraudDetectionResult.mediumRisk(
                    60,
                    "Repeated failed transactions detected",
                    "REPEATED_FAILURES"
            );
        }

        // No scenario matched
        return null;
    }
}
