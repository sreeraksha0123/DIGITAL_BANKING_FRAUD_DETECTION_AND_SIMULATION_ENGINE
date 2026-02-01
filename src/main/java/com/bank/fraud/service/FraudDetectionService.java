package com.bank.fraud.service;

import com.bank.fraud.dto.FraudScoreDTO;
import com.bank.fraud.dto.TransactionRequestDTO;
import com.bank.fraud.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final MLFraudScoringService mlFraudScoringService;
    private final RiskScoringService riskScoringService;

    public FraudDetectionService(
            TransactionRepository transactionRepository,
            MLFraudScoringService mlFraudScoringService,
            RiskScoringService riskScoringService
    ) {
        this.transactionRepository = transactionRepository;
        this.mlFraudScoringService = mlFraudScoringService;
        this.riskScoringService = riskScoringService;
    }

    // --------------------------------------------------
    // MAIN FRAUD EVALUATION METHOD
    // --------------------------------------------------

    public FraudScoreDTO evaluateFraud(TransactionRequestDTO request) {

        List<String> ruleTriggers = new ArrayList<>();
        int ruleScore = 0;

        // 1️⃣ Amount-based rule
        if (request.getAmount() > 100000) {
            ruleScore += 40;
            ruleTriggers.add("Transaction amount exceeds ₹100,000");
        } else if (request.getAmount() > 50000) {
            ruleScore += 25;
            ruleTriggers.add("Transaction amount exceeds ₹50,000");
        } else if (request.getAmount() > 20000) {
            ruleScore += 15;
            ruleTriggers.add("Transaction amount exceeds ₹20,000");
        } else if (request.getAmount() > 10000) {
            ruleScore += 8;
            ruleTriggers.add("Transaction amount exceeds ₹10,000");
        }

        // 2️⃣ Velocity rule (multiple transactions in short time)
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        long recentTxnCount = transactionRepository.countRecentTransactions(
                request.getAccountId(),
                fiveMinutesAgo
        );

        if (recentTxnCount >= 3) {
            ruleScore += 30;
            ruleTriggers.add("Multiple transactions in a short time window");
        }

        // 3️⃣ Late-night transaction rule
        int hour = LocalDateTime.now().getHour();
        if (hour >= 23 || hour <= 4) {
            ruleScore += 15;
            ruleTriggers.add("Transaction occurred during late-night hours");
        }

        // 4️⃣ Location anomaly rule (simple simulation)
        if ("UNKNOWN".equalsIgnoreCase(request.getCity())) {
            ruleScore += 25;
            ruleTriggers.add("Unrecognized transaction location");
        }

        // Cap rule score at 100
        ruleScore = Math.min(ruleScore, 100);

        // 5️⃣ ML fraud probability (0–100)
        int mlScore = mlFraudScoringService.predictFraudScore(request);

        // 6️⃣ Final risk scoring & classification
        FraudScoreDTO fraudScoreDTO = riskScoringService.calculateFinalRisk(
                ruleScore,
                mlScore,
                ruleTriggers
        );

        return fraudScoreDTO;
    }
}
