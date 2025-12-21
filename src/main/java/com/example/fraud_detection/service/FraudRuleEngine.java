package com.example.fraud_detection.service;

import com.example.fraud_detection.config.FraudRulesConfig;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FraudRuleEngine {

    private final TransactionRepository transactionRepository;
    private final FraudRulesConfig rulesConfig;

    public FraudRuleEngine(TransactionRepository transactionRepository,
                           FraudRulesConfig rulesConfig) {
        this.transactionRepository = transactionRepository;
        this.rulesConfig = rulesConfig;
    }

    public int calculateFraudScore(Transaction transaction) {
        int fraudScore = 0;
        StringBuilder reason = new StringBuilder();
        String fraudType = "";

        // Rule 1: Amount-based rules
        if (transaction.getAmount() != null) {
            if (transaction.getAmount() > rulesConfig.getVeryHighAmountThreshold()) {
                fraudScore += rulesConfig.getVeryHighAmountScore();
                reason.append("Very high amount (").append(transaction.getAmount()).append("); ");
                fraudType = addFraudType(fraudType, "AMOUNT_ABUSE");
            } else if (transaction.getAmount() > rulesConfig.getHighAmountThreshold()) {
                fraudScore += rulesConfig.getHighAmountScore();
                reason.append("High amount (").append(transaction.getAmount()).append("); ");
                fraudType = addFraudType(fraudType, "AMOUNT_ABUSE");
            }
        }

        // Rule 2: Transaction type
        if ("WITHDRAW".equalsIgnoreCase(transaction.getTransactionType())) {
            fraudScore += rulesConfig.getWithdrawalScore();
            reason.append("Withdrawal transaction; ");
            fraudType = addFraudType(fraudType, "WITHDRAWAL_RISK");
        }

        // Rule 3: Failed transactions
        if (Boolean.FALSE.equals(transaction.getSuccessStatus())) {
            fraudScore += rulesConfig.getFailedTransactionScore();
            reason.append("Failed transaction; ");
            fraudType = addFraudType(fraudType, "FAILED_TX_PATTERN");
        }

        // Rule 4: Location-based rules
        if (transaction.getLocation() != null) {
            String location = transaction.getLocation().toLowerCase();
            if (location.contains("unknown") || location.contains("tor") ||
                    location.contains("proxy") || location.contains("vpn")) {
                fraudScore += rulesConfig.getSuspiciousLocationScore();
                reason.append("Suspicious location (").append(transaction.getLocation()).append("); ");
                fraudType = addFraudType(fraudType, "LOCATION_RISK");
            }
        }

        // Rule 5: Time-based rules
        if (Boolean.TRUE.equals(transaction.getIsNightTime())) {
            fraudScore += rulesConfig.getNightTimeScore();
            reason.append("Night time transaction; ");
            fraudType = addFraudType(fraudType, "TIMING_RISK");
        }

        // Rule 6: Velocity checking (multiple transactions in short time)
        if (transaction.getAccountNumber() != null && transaction.getTransactionTime() != null) {
            LocalDateTime oneHourAgo = transaction.getTransactionTime().minusHours(1);
            List<Transaction> recentTransactions = transactionRepository
                    .findByAccountNumberAndTransactionTimeAfter(
                            transaction.getAccountNumber(), oneHourAgo);

            if (recentTransactions.size() > rulesConfig.getMaxTransactionsPerHour()) {
                fraudScore += rulesConfig.getVelocityScore();
                reason.append("High velocity - ")
                        .append(recentTransactions.size())
                        .append(" transactions in last hour; ");
                fraudType = addFraudType(fraudType, "VELOCITY_RISK");
            }

            // Calculate average and check for anomaly
            double avgAmount = recentTransactions.stream()
                    .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0)
                    .average()
                    .orElse(0.0);

            if (transaction.getAmount() != null &&
                    transaction.getAmount() > avgAmount * rulesConfig.getVelocityThresholdMultiplier()) {
                fraudScore += 15;
                reason.append("Amount significantly higher than average; ");
                fraudType = addFraudType(fraudType, "AMOUNT_ANOMALY");
            }
        }

        // Rule 7: Device/IP mismatch
        if (transaction.getUserId() != null && transaction.getDeviceId() != null) {
            // In real system, you would check against user's known devices
            List<Transaction> userTransactions = transactionRepository
                    .findByUserId(transaction.getUserId());

            boolean knownDevice = userTransactions.stream()
                    .anyMatch(t -> transaction.getDeviceId().equals(t.getDeviceId()));

            if (!knownDevice && userTransactions.size() > 5) {
                fraudScore += rulesConfig.getDeviceMismatchScore();
                reason.append("New/unrecognized device; ");
                fraudType = addFraudType(fraudType, "DEVICE_RISK");
            }
        }

        // Rule 8: IP Address analysis
        if (transaction.getIpAddress() != null) {
            // Check for suspicious IP patterns
            if (isSuspiciousIp(transaction.getIpAddress())) {
                fraudScore += rulesConfig.getIpMismatchScore();
                reason.append("Suspicious IP address; ");
                fraudType = addFraudType(fraudType, "IP_RISK");
            }

            // Check IP-country mismatch
            if (transaction.getCountry() != null &&
                    !isIpFromCountry(transaction.getIpAddress(), transaction.getCountry())) {
                fraudScore += 15;
                reason.append("IP-country mismatch; ");
                fraudType = addFraudType(fraudType, "GEO_MISMATCH");
            }
        }

        // Set the results
        transaction.setFraudScore(fraudScore);
        transaction.setFraudType(fraudType.isEmpty() ? "NONE" : fraudType);
        transaction.setFraudReason(reason.length() > 0 ?
                reason.toString() : "Transaction appears normal");

        return fraudScore;
    }

    private String addFraudType(String current, String newType) {
        if (current == null || current.isEmpty()) {
            return newType;
        }
        if (!current.contains(newType)) {
            return current + "," + newType;
        }
        return current;
    }

    private boolean isSuspiciousIp(String ipAddress) {
        // Simplified check - in production, use IP reputation service
        return ipAddress.startsWith("192.168.") ||
                ipAddress.startsWith("10.") ||
                ipAddress.contains(":") || // IPv6
                ipAddress.equals("127.0.0.1") ||
                ipAddress.matches(".*\\.(tor|proxy|vpn)\\..*");
    }

    // Add this method to FraudRuleEngine class if missing:
    public List<Transaction> findByAccountNumberAndTransactionTimeAfter(String accountNumber, LocalDateTime time) {
        return transactionRepository.findByAccountNumberAndTransactionTimeAfter(accountNumber, time);
    }

    private boolean isIpFromCountry(String ipAddress, String country) {
        // In production, use GeoIP service
        // This is a simplified implementation
        return true; // Assume match for now
    }

    public String determineRiskLevel(int fraudScore) {
        if (fraudScore >= rulesConfig.getHighRiskThreshold()) {
            return "HIGH";
        } else if (fraudScore >= rulesConfig.getMediumRiskThreshold()) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public String getRecommendation(int fraudScore, String riskLevel) {
        switch (riskLevel) {
            case "HIGH":
                return "BLOCK: Transaction requires manual review. Notify security team.";
            case "MEDIUM":
                return "REVIEW: Transaction suspicious. Request additional verification.";
            case "LOW":
                return "ALLOW: Transaction appears legitimate. Monitor for patterns.";
            default:
                return "ALLOW: Proceed with transaction.";
        }
    }
}