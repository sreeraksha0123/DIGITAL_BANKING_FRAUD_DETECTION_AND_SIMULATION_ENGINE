package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.FraudDetectionResult;
import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdvancedFraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(AdvancedFraudDetectionService.class);

    private final TransactionRepository transactionRepository;
    private final FraudRuleEngine fraudRuleEngine;
    private final EmailAlertService emailAlertService;

    public AdvancedFraudDetectionService(TransactionRepository transactionRepository,
                                         FraudRuleEngine fraudRuleEngine,
                                         EmailAlertService emailAlertService) {
        this.transactionRepository = transactionRepository;
        this.fraudRuleEngine = fraudRuleEngine;
        this.emailAlertService = emailAlertService;
    }

    @Transactional
    public FraudDetectionResult analyzeTransaction(TransactionRequest request) {
        logger.info("Analyzing transaction for account: {}", request.getAccountNumber());

        // Validate request
        if (request.getAccountNumber() == null || request.getAccountNumber().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        // Convert request to entity
        Transaction transaction = convertToEntity(request);

        // Apply fraud detection rules
        int fraudScore = fraudRuleEngine.calculateFraudScore(transaction);
        String riskLevel = fraudRuleEngine.determineRiskLevel(fraudScore);
        String recommendation = fraudRuleEngine.getRecommendation(fraudScore, riskLevel);
        String fraudType = transaction.getFraudType();

        // Determine if fraud
        boolean isFraud = riskLevel.equals("HIGH") || riskLevel.equals("MEDIUM");
        transaction.setIsFraud(isFraud);
        transaction.setRiskLevel(riskLevel);
        transaction.setFraudScore(fraudScore);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction saved with ID: {}, Fraud Score: {}, Risk: {}",
                savedTransaction.getId(),
                fraudScore,
                riskLevel);

        // Send alerts if high or medium risk
        if (riskLevel.equals("HIGH") || riskLevel.equals("MEDIUM")) {
            emailAlertService.sendFraudAlert(savedTransaction);
            logger.warn("{} RISK transaction detected: ID={}, Account={}, Score={}",
                    riskLevel, savedTransaction.getId(), savedTransaction.getAccountNumber(), fraudScore);
        }

        // Log the transaction for audit
        logTransactionAudit(savedTransaction, fraudScore, riskLevel, isFraud);

        // Return result
        return new FraudDetectionResult(
                savedTransaction.getId(),
                isFraud,
                fraudType != null ? fraudType : "NONE",
                fraudScore,
                riskLevel,
                transaction.getFraudReason() != null ? transaction.getFraudReason() : "No issues detected",
                recommendation,
                "PROCESSED_SUCCESSFULLY"
        );
    }

    public TransactionResponse processTransaction(TransactionRequest request) {
        try {
            FraudDetectionResult result = analyzeTransaction(request);

            return new TransactionResponse(
                    result.getId(),
                    result.isFraud(),
                    result.getFraudType(),
                    result.getFraudScore(),
                    result.getRiskLevel(),
                    result.getFraudReason(),
                    result.getRecommendation()
            );
        } catch (Exception e) {
            logger.error("Error processing transaction for account: {}", request.getAccountNumber(), e);

            // Return error response
            return new TransactionResponse(
                    null,
                    false,
                    "PROCESSING_ERROR",
                    0,
                    "ERROR",
                    "Failed to process transaction: " + e.getMessage(),
                    "Please try again or contact support"
            );
        }
    }

    private Transaction convertToEntity(TransactionRequest request) {
        Transaction transaction = new Transaction();

        // Basic transaction details
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        transaction.setTransactionType(request.getTransactionType() != null ?
                request.getTransactionType().toUpperCase() : "TRANSFER");

        // Additional details
        transaction.setMerchantId(request.getMerchantId());
        transaction.setDeviceId(request.getDeviceId());
        transaction.setIpAddress(request.getIpAddress());

        // Location details
        transaction.setLocation(request.getLocation());
        transaction.setCountry(request.getCountry());
        transaction.setCity(request.getCity());

        // Timing
        transaction.setTransactionTime(request.getTransactionTime() != null ?
                request.getTransactionTime() : LocalDateTime.now());

        // User information
        transaction.setUserId(request.getUserId());

        // Default values
        transaction.setSuccessStatus(true); // Default to success
        transaction.setFraudReason("Analysis pending");
        transaction.setIsFraud(false); // Default to not fraud

        return transaction;
    }

    // Batch analysis for multiple transactions
    @Transactional
    public List<FraudDetectionResult> analyzeTransactionBatch(List<TransactionRequest> requests) {
        return requests.stream()
                .map(request -> {
                    try {
                        return analyzeTransaction(request);
                    } catch (Exception e) {
                        logger.error("Error analyzing transaction: {}", request.getAccountNumber(), e);
                        return new FraudDetectionResult(
                                null,
                                false,
                                "PROCESSING_ERROR",
                                0,
                                "ERROR",
                                "Failed to analyze: " + e.getMessage(),
                                "Review transaction details",
                                "FAILED"
                        );
                    }
                })
                .toList();
    }

    // Enhanced batch processing with statistics
    public BatchProcessingResult analyzeTransactionBatchWithStats(List<TransactionRequest> requests) {
        List<FraudDetectionResult> results = analyzeTransactionBatch(requests);

        long total = results.size();
        long fraudCount = results.stream().filter(FraudDetectionResult::isFraud).count();
        long highRiskCount = results.stream().filter(r -> "HIGH".equals(r.getRiskLevel())).count();
        long mediumRiskCount = results.stream().filter(r -> "MEDIUM".equals(r.getRiskLevel())).count();
        long lowRiskCount = results.stream().filter(r -> "LOW".equals(r.getRiskLevel())).count();
        long failedCount = results.stream().filter(r -> "FAILED".equals(r.getStatus())).count();

        double fraudPercentage = total > 0 ? (double) fraudCount / total * 100 : 0;

        return new BatchProcessingResult(
                total,
                fraudCount,
                highRiskCount,
                mediumRiskCount,
                lowRiskCount,
                failedCount,
                fraudPercentage,
                results
        );
    }

    // Get transaction statistics
    public TransactionStatistics getTransactionStatistics() {
        long totalTransactions = transactionRepository.count();
        long fraudTransactions = transactionRepository.countByIsFraudTrue();
        long highRiskTransactions = transactionRepository.countByRiskLevel("HIGH");
        long mediumRiskTransactions = transactionRepository.countByRiskLevel("MEDIUM");

        double fraudPercentage = totalTransactions > 0 ?
                (double) fraudTransactions / totalTransactions * 100 : 0;

        Double avgFraudScore = transactionRepository.findAverageFraudScore();

        return new TransactionStatistics(
                totalTransactions,
                fraudTransactions,
                highRiskTransactions,
                mediumRiskTransactions,
                fraudPercentage,
                avgFraudScore != null ? avgFraudScore : 0.0
        );
    }

    // Audit logging
    private void logTransactionAudit(Transaction transaction, int fraudScore, String riskLevel, boolean isFraud) {
        logger.info("""
                📊 TRANSACTION AUDIT LOG
                ==========================================
                Transaction ID: {}
                Account: {}
                Amount: {}
                Type: {}
                Location: {}
                Fraud Score: {}
                Risk Level: {}
                Is Fraud: {}
                Fraud Reason: {}
                Time: {}
                ==========================================
                """,
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getLocation(),
                fraudScore,
                riskLevel,
                isFraud,
                transaction.getFraudReason(),
                transaction.getTransactionTime()
        );
    }

    // Inner classes for statistics and batch results
    public static class BatchProcessingResult {
        private final long totalTransactions;
        private final long fraudCount;
        private final long highRiskCount;
        private final long mediumRiskCount;
        private final long lowRiskCount;
        private final long failedCount;
        private final double fraudPercentage;
        private final List<FraudDetectionResult> results;

        public BatchProcessingResult(long totalTransactions, long fraudCount, long highRiskCount,
                                     long mediumRiskCount, long lowRiskCount, long failedCount,
                                     double fraudPercentage, List<FraudDetectionResult> results) {
            this.totalTransactions = totalTransactions;
            this.fraudCount = fraudCount;
            this.highRiskCount = highRiskCount;
            this.mediumRiskCount = mediumRiskCount;
            this.lowRiskCount = lowRiskCount;
            this.failedCount = failedCount;
            this.fraudPercentage = fraudPercentage;
            this.results = results;
        }

        // Getters
        public long getTotalTransactions() { return totalTransactions; }
        public long getFraudCount() { return fraudCount; }
        public long getHighRiskCount() { return highRiskCount; }
        public long getMediumRiskCount() { return mediumRiskCount; }
        public long getLowRiskCount() { return lowRiskCount; }
        public long getFailedCount() { return failedCount; }
        public double getFraudPercentage() { return fraudPercentage; }
        public List<FraudDetectionResult> getResults() { return results; }
    }

    public static class TransactionStatistics {
        private final long totalTransactions;
        private final long fraudTransactions;
        private final long highRiskTransactions;
        private final long mediumRiskTransactions;
        private final double fraudPercentage;
        private final double averageFraudScore;

        public TransactionStatistics(long totalTransactions, long fraudTransactions,
                                     long highRiskTransactions, long mediumRiskTransactions,
                                     double fraudPercentage, double averageFraudScore) {
            this.totalTransactions = totalTransactions;
            this.fraudTransactions = fraudTransactions;
            this.highRiskTransactions = highRiskTransactions;
            this.mediumRiskTransactions = mediumRiskTransactions;
            this.fraudPercentage = fraudPercentage;
            this.averageFraudScore = averageFraudScore;
        }

        // Getters
        public long getTotalTransactions() { return totalTransactions; }
        public long getFraudTransactions() { return fraudTransactions; }
        public long getHighRiskTransactions() { return highRiskTransactions; }
        public long getMediumRiskTransactions() { return mediumRiskTransactions; }
        public double getFraudPercentage() { return fraudPercentage; }
        public double getAverageFraudScore() { return averageFraudScore; }
    }
}