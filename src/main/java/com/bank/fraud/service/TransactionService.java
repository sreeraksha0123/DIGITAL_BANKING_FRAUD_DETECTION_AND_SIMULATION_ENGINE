package com.bank.fraud.service;

import com.bank.fraud.dto.FraudScoreDTO;
import com.bank.fraud.dto.TransactionRequestDTO;
import com.bank.fraud.dto.TransactionResponseDTO;
import com.bank.fraud.model.Transaction;
import com.bank.fraud.repository.TransactionRepository;
import com.bank.fraud.repository.BlockedAccountRepository;
import com.bank.fraud.repository.AuditLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;
    private final RiskScoringService riskScoringService;
    private final AccountBlockService accountBlockService;
    private final AlertService alertService;
    private final AuditLogService auditLogService;

    public TransactionService(
            TransactionRepository transactionRepository,
            FraudDetectionService fraudDetectionService,
            RiskScoringService riskScoringService,
            AccountBlockService accountBlockService,
            AlertService alertService,
            AuditLogService auditLogService
    ) {
        this.transactionRepository = transactionRepository;
        this.fraudDetectionService = fraudDetectionService;
        this.riskScoringService = riskScoringService;
        this.accountBlockService = accountBlockService;
        this.alertService = alertService;
        this.auditLogService = auditLogService;
    }

    // --------------------------------------------------
    // MAIN TRANSACTION PROCESSING METHOD
    // --------------------------------------------------

    @Transactional
    public TransactionResponseDTO processTransaction(TransactionRequestDTO request) {

        long startTime = System.currentTimeMillis();

        // 1️⃣ Prevent duplicate transaction processing
        if (transactionRepository.existsByTransactionId(request.getTransactionId())) {
            throw new IllegalArgumentException("Duplicate transaction detected");
        }

        // 2️⃣ Check if account is already blocked
        if (accountBlockService.isAccountBlocked(request.getAccountId())) {
            throw new IllegalStateException("Account is temporarily blocked");
        }

        // 3️⃣ Perform fraud detection (rules + ML)
        FraudScoreDTO fraudScore = fraudDetectionService.evaluateFraud(request);

        // 4️⃣ Decide final transaction status
        String status;
        if ("HIGH".equals(fraudScore.getRiskLevel())) {
            status = "BLOCKED";
            accountBlockService.blockAccount(
                    request.getAccountId(),
                    fraudScore.getRuleTriggers()
            );
        } else {
            status = "SUCCESS";
        }

        // 5️⃣ Persist transaction
        Transaction transaction = buildTransactionEntity(
                request,
                fraudScore,
                status,
                startTime
        );
        transactionRepository.save(transaction);

        // 6️⃣ Trigger alerts if fraud detected
        if (fraudScore.getFraudDetected()) {
            alertService.sendFraudAlert(transaction);
        }

        // 7️⃣ Write audit log
        auditLogService.logTransactionEvent(transaction);

        // 8️⃣ Build response DTO
        return buildResponseDTO(transaction);
    }

    // --------------------------------------------------
    // HELPER METHODS
    // --------------------------------------------------

    private Transaction buildTransactionEntity(
            TransactionRequestDTO request,
            FraudScoreDTO fraudScore,
            String status,
            long startTime
    ) {
        Transaction tx = new Transaction();
        tx.setTransactionId(request.getTransactionId());
        tx.setAccountId(request.getAccountId());
        tx.setCustomerName(request.getCustomerName());
        tx.setAmount(request.getAmount());
        tx.setTransactionType(request.getTransactionType());
        tx.setCity(request.getCity());
        tx.setIpAddress(request.getIpAddress());
        tx.setDeviceId(request.getDeviceId());

        tx.setRuleScore(fraudScore.getRuleScore());
        tx.setMlScore(fraudScore.getMlScore());
        tx.setFinalRiskScore(fraudScore.getFinalRiskScore());
        tx.setRiskLevel(fraudScore.getRiskLevel());
        tx.setFraudDetected(fraudScore.getFraudDetected());

        tx.setStatus(status);
        tx.setTransactionTime(LocalDateTime.now());
        tx.setProcessingTimeMs(System.currentTimeMillis() - startTime);

        return tx;
    }

    private TransactionResponseDTO buildResponseDTO(Transaction transaction) {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setTransactionId(transaction.getTransactionId());
        response.setAccountId(transaction.getAccountId());
        response.setAmount(transaction.getAmount());
        response.setTransactionType(transaction.getTransactionType());
        response.setCity(transaction.getCity());

        response.setRuleScore(transaction.getRuleScore());
        response.setMlScore(transaction.getMlScore());
        response.setFinalRiskScore(transaction.getFinalRiskScore());
        response.setRiskLevel(transaction.getRiskLevel());

        response.setStatus(transaction.getStatus());
        response.setFraudDetected(transaction.getFraudDetected());
        response.setProcessingTimeMs(transaction.getProcessingTimeMs());
        response.setTransactionTime(transaction.getTransactionTime());

        return response;
    }
}
