package com.example.fraud_detection.service;

import com.example.fraud_detection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides authoritative fraud metrics.
 *
 * All values come from persisted transactions.
 * No business logic, no recalculation.
 */
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final TransactionRepository transactionRepository;

    /**
     * Returns all dashboard metrics in a single payload.
     */
    public Map<String, Object> getDashboardMetrics() {

        long totalTransactions = transactionRepository.count();
        long fraudTransactions = transactionRepository.countByIsFraudTrue();
        long highRiskTransactions = transactionRepository.countHighRiskTransactions();

        double fraudRate =
                totalTransactions == 0
                        ? 0.0
                        : (fraudTransactions * 100.0) / totalTransactions;

        Double blockedAmount =
                transactionRepository.getTotalBlockedAmount();

        Double avgFraudScore =
                transactionRepository.getAverageFraudScore();

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalTransactions", totalTransactions);
        metrics.put("fraudTransactions", fraudTransactions);
        metrics.put("fraudRate", fraudRate);
        metrics.put("highRiskTransactions", highRiskTransactions);
        metrics.put("blockedAmount", blockedAmount);
        metrics.put("averageFraudScore", avgFraudScore);

        return metrics;
    }
}
