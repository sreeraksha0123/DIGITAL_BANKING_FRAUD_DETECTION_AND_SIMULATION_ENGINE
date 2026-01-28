package com.example.fraud_detection.service;

import com.example.fraud_detection.dto.FraudDetectionResult;
import com.example.fraud_detection.dto.MlPredictionResult;
import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central fraud detection orchestrator.
 *
 * ARCHITECTURE:
 * This service orchestrates THREE independent fraud detection mechanisms:
 *
 * 1. RULE ENGINE (deterministic)
 *    - Predefined business rules
 *    - Amount, location, timing, velocity
 *    - Authority: HIGH (overrides ML if triggered)
 *
 * 2. ML PREDICTION (probabilistic)
 *    - Machine learning model scores
 *    - Behavioral pattern analysis
 *    - Authority: MEDIUM (advisory, signals patterns)
 *
 * 3. SCENARIO ENGINE (contextual)
 *    - Complex fraud scenarios
 *    - Multi-transaction patterns
 *    - Authority: HIGHEST (overrides both)
 *
 * DECISION PRIORITY:
 * SCENARIO > RULES > ML > LEGITIMATE
 *
 * This ensures:
 * - Complex fraud scenarios are caught first
 * - Clear business rules are respected
 * - ML enhances detection without overriding policy
 * - Default assumption is legitimate unless proven otherwise
 */
@Service
@RequiredArgsConstructor
public class AdvancedFraudDetectionService {

    private final FraudRuleEngine fraudRuleEngine;
    private final MlPredictionService mlPredictionService;
    private final FraudScenarioService fraudScenarioService;
    private final TransactionRepository transactionRepository;

    /**
     * Process a transaction through the complete fraud detection pipeline.
     *
     * STEPS:
     * 1. Create transaction entity (no fraud logic)
     * 2. Evaluate rule-based detection
     * 3. Run ML prediction
     * 4. Check predefined fraud scenarios
     * 5. Resolve final decision using priority
     * 6. Apply decision to transaction
     * 7. Persist to database
     */
    @Transactional
    public Transaction processTransaction(TransactionRequest request) {

        // ================================================================
        // STEP 1: Build transaction entity (data only, no fraud logic)
        // ================================================================
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setLocation(request.getLocation());
        transaction.setCountry(request.getCountry());
        transaction.setCity(request.getCity());
        transaction.setTransactionTime(request.getTransactionTime());

        // ================================================================
        // STEP 2: Rule-based evaluation (deterministic, authority HIGH)
        // ================================================================
        FraudDetectionResult ruleResult = fraudRuleEngine.evaluate(transaction);

        // ================================================================
        // STEP 3: ML prediction (probabilistic, authority MEDIUM)
        // ================================================================
        MlPredictionResult mlResult = mlPredictionService.predict(transaction);

        // ================================================================
        // STEP 4: Scenario-based evaluation (contextual, authority HIGHEST)
        // ================================================================
        FraudDetectionResult scenarioResult = fraudScenarioService.evaluate(transaction);

        // ================================================================
        // STEP 5: Resolve final decision with priority logic
        // ================================================================
        FraudDetectionResult finalResult = resolveFinalDecision(
                ruleResult,
                mlResult,
                scenarioResult
        );

        // ================================================================
        // STEP 6: Apply final fraud decision to transaction
        // ================================================================
        transaction.applyFraudDecision(
                finalResult.getFraudScore(),
                finalResult.getRiskLevel(),
                finalResult.isFraud(),
                finalResult.getFraudReason(),
                finalResult.getFraudType()
        );

        // ================================================================
        // STEP 7: Persist transaction with final decision
        // ================================================================
        return transactionRepository.save(transaction);
    }

    /**
     * Determine final fraud decision using three-tier priority system.
     *
     * PRIORITY ORDER:
     * 1. SCENARIO DETECTION (highest authority)
     *    - Complex multi-transaction fraud patterns
     *    - Cannot be overridden
     *
     * 2. RULE ENGINE (high authority)
     *    - Business policy rules
     *    - Explicit risk thresholds
     *    - Cannot be overridden by ML
     *
     * 3. ML SIGNALS (medium authority)
     *    - Probabilistic risk indicators
     *    - Score >= 70 flags as medium risk
     *    - Can be overridden by rules
     *
     * 4. DEFAULT (lowest)
     *    - Assume legitimate unless proven otherwise
     *    - Score 0-29 = legitimate
     */
    private FraudDetectionResult resolveFinalDecision(
            FraudDetectionResult ruleResult,
            MlPredictionResult mlResult,
            FraudDetectionResult scenarioResult
    ) {

        // ================================================================
        // TIER 1: Scenario detection (HIGHEST PRIORITY)
        // ================================================================
        if (scenarioResult != null && scenarioResult.isFraud()) {
            // Scenarios override everything
            // Example: rapid sequence of large international transfers
            return scenarioResult;
        }

        // ================================================================
        // TIER 2: Rule engine detection (HIGH PRIORITY)
        // ================================================================
        if (ruleResult != null && ruleResult.isFraud()) {
            // Rules are authoritative business policy
            // Example: $250k international transfer at 3 AM = HIGH RISK
            return ruleResult;
        }

        // Check if rule returned medium risk
        if (ruleResult != null && ruleResult.getRiskLevel().equals("MEDIUM")) {
            return ruleResult;
        }

        // ================================================================
        // TIER 3: ML signals (MEDIUM PRIORITY, ADVISORY ONLY)
        // ================================================================
        if (mlResult != null && mlResult.getRiskScore() >= 70) {
            // ML HIGH signal: flag as medium risk for review
            // Note: ML does NOT trigger HIGH RISK by itself
            // This ensures human oversight on ML-only detections
            return FraudDetectionResult.mediumRisk(
                    mlResult.getRiskScore(),
                    "ML model detected suspicious behavioral patterns: " + mlResult.getExplanation(),
                    "ML_SIGNAL"
            );
        }

        if (mlResult != null && mlResult.getRiskScore() >= 50) {
            // ML MEDIUM signal: slight elevation, monitor
            return FraudDetectionResult.mediumRisk(
                    mlResult.getRiskScore(),
                    "ML model flagged elevated risk: " + mlResult.getExplanation(),
                    "ML_SIGNAL"
            );
        }

        // ================================================================
        // TIER 4: Default (LOWEST PRIORITY)
        // ================================================================
        // No signals from any detector → assume legitimate
        return FraudDetectionResult.legitimate();
    }
}