package com.bank.fraud.service;

import com.bank.fraud.dto.FraudScoreDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskScoringService {

    // Weight configuration (can be externalized later)
    private static final double RULE_WEIGHT = 0.4;
    private static final double ML_WEIGHT = 0.6;

    // Risk thresholds
    private static final int LOW_RISK_MAX = 29;
    private static final int MEDIUM_RISK_MAX = 59;

    // --------------------------------------------------
    // FINAL RISK CALCULATION
    // --------------------------------------------------

    public FraudScoreDTO calculateFinalRisk(
            int ruleScore,
            int mlScore,
            List<String> ruleTriggers
    ) {

        // 1️⃣ Calculate weighted final risk score
        int finalRiskScore = (int) Math.round(
                (ruleScore * RULE_WEIGHT) + (mlScore * ML_WEIGHT)
        );

        // 2️⃣ Determine risk level
        String riskLevel;
        boolean fraudDetected;

        if (finalRiskScore <= LOW_RISK_MAX) {
            riskLevel = "LOW";
            fraudDetected = false;
        } else if (finalRiskScore <= MEDIUM_RISK_MAX) {
            riskLevel = "MEDIUM";
            fraudDetected = false;
        } else {
            riskLevel = "HIGH";
            fraudDetected = true;
        }

        // 3️⃣ Build FraudScoreDTO
        FraudScoreDTO fraudScoreDTO = new FraudScoreDTO();
        fraudScoreDTO.setRuleScore(ruleScore);
        fraudScoreDTO.setMlScore(mlScore);
        fraudScoreDTO.setFinalRiskScore(finalRiskScore);
        fraudScoreDTO.setRiskLevel(riskLevel);
        fraudScoreDTO.setFraudDetected(fraudDetected);
        fraudScoreDTO.setRuleTriggers(ruleTriggers);

        return fraudScoreDTO;
    }
}
