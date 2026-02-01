package com.bank.fraud.service;

import com.bank.fraud.dto.AnalyticsDTO;
import com.bank.fraud.model.Transaction;
import com.bank.fraud.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final TransactionRepository transactionRepository;

    public AnalyticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // --------------------------------------------------
    // MAIN ANALYTICS AGGREGATION METHOD
    // --------------------------------------------------

    public AnalyticsDTO getSystemAnalytics() {

        AnalyticsDTO analytics = new AnalyticsDTO();

        // 1️⃣ Basic counts
        long totalTransactions = transactionRepository.count();
        long highRiskCount = transactionRepository.countByRiskLevel("HIGH");
        long mediumRiskCount = transactionRepository.countByRiskLevel("MEDIUM");
        long lowRiskCount = transactionRepository.countByRiskLevel("LOW");

        // 2️⃣ Fraud transactions
        List<Transaction> fraudTransactions =
                transactionRepository.findFraudTransactions();
        long fraudCount = fraudTransactions.size();

        // 3️⃣ Success / failure counts
        long successCount = transactionRepository.findByStatus("SUCCESS").size();
        long failedCount = transactionRepository.findByStatus("FAILED").size();

        // 4️⃣ Fraud rate calculation
        double fraudRate = totalTransactions == 0
                ? 0.0
                : ((double) fraudCount / totalTransactions) * 100;

        // 5️⃣ Average fraud score
        double avgFraudScore = fraudTransactions.stream()
                .mapToInt(Transaction::getFinalRiskScore)
                .average()
                .orElse(0.0);

        // 6️⃣ Detection accuracy (simulated)
        double detectionAccuracy = 96.2;

        // 7️⃣ Populate DTO
        analytics.setTotalTransactions(totalTransactions);
        analytics.setSuccessfulTransactions(successCount);
        analytics.setFailedTransactions(failedCount);
        analytics.setFraudTransactions(fraudCount);

        analytics.setFraudRatePercentage(round(fraudRate));
        analytics.setAverageFraudScore(round(avgFraudScore));
        analytics.setDetectionAccuracy(detectionAccuracy);

        analytics.setLowRiskCount(lowRiskCount);
        analytics.setMediumRiskCount(mediumRiskCount);
        analytics.setHighRiskCount(highRiskCount);

        return analytics;
    }

    // --------------------------------------------------
    // HELPER METHOD
    // --------------------------------------------------

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
