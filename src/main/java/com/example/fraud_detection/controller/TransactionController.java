package com.example.fraud_detection.controller;

import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import com.example.fraud_detection.service.AdvancedFraudDetectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AdvancedFraudDetectionService fraudDetectionService;

    public TransactionController(TransactionRepository transactionRepository,
                                 AdvancedFraudDetectionService fraudDetectionService) {
        this.transactionRepository = transactionRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest transactionRequest) {

        TransactionResponse response = fraudDetectionService.processTransaction(transactionRequest);

        // ===== FINAL CORRECTED LOGIC =====
        // Set analysis status to COMPLETED (API successfully analyzed)
        response.setAnalysisStatus("COMPLETED");

        // Get HTTP status based on APPROVAL decision
        // SUCCESS (201)  → Approved, process it
        // PENDING (202)  → Waiting for review
        // FAILURE (403)  → Blocked due to fraud
        HttpStatus status = getHttpStatusFromApprovalStatus(response.getApprovalStatus());

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> createTransactionsBatch(
            @RequestBody List<TransactionRequest> transactionRequests) {

        var result = fraudDetectionService.analyzeTransactionBatchWithStats(transactionRequests);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/fraud/all")
    public ResponseEntity<List<Transaction>> getFraudulentTransactions() {
        List<Transaction> fraudTransactions = transactionRepository.findByIsFraudTrue();
        return ResponseEntity.ok(fraudTransactions);
    }

    @GetMapping("/risk/high")
    public ResponseEntity<List<Transaction>> getHighRiskTransactions() {
        List<Transaction> highRiskTransactions = transactionRepository.findByRiskLevel("HIGH");
        return ResponseEntity.ok(highRiskTransactions);
    }

    @GetMapping("/risk/medium")
    public ResponseEntity<List<Transaction>> getMediumRiskTransactions() {
        List<Transaction> mediumRiskTransactions = transactionRepository.findByRiskLevel("MEDIUM");
        return ResponseEntity.ok(mediumRiskTransactions);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getFraudStats() {
        var stats = fraudDetectionService.getTransactionStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(
            @PathVariable String accountNumber) {
        List<Transaction> transactions = transactionRepository.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(transactions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ===== HELPER METHOD - Maps approval status to HTTP code =====
    /**
     * Converts approval status to appropriate HTTP status code.
     *
     * SUCCESS  → 201 Created   (transaction approved and will be processed)
     * PENDING  → 202 Accepted  (transaction pending review)
     * FAILURE  → 403 Forbidden (transaction blocked due to fraud)
     */
    private HttpStatus getHttpStatusFromApprovalStatus(String approvalStatus) {
        if (approvalStatus == null) {
            return HttpStatus.OK;
        }

        return switch (approvalStatus) {
            case "SUCCESS" -> HttpStatus.CREATED;           // 201 - Approved
            case "PENDING" -> HttpStatus.ACCEPTED;          // 202 - Pending review
            case "FAILURE" -> HttpStatus.FORBIDDEN;         // 403 - Blocked/Fraud
            default -> HttpStatus.OK;                        // 200 - Fallback
        };
    }
}