package com.example.fraud_detection.controller;

import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import com.example.fraud_detection.service.AdvancedFraudDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AdvancedFraudDetectionService fraudService;

    public TransactionController(
            TransactionRepository transactionRepository,
            AdvancedFraudDetectionService fraudService
    ) {
        this.transactionRepository = transactionRepository;
        this.fraudService = fraudService;
    }

    // ============================
    // CREATE TRANSACTION
    // ============================
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request
    ) {
        Transaction transaction = fraudService.processTransaction(request);
        return ResponseEntity.ok(toResponse(transaction));
    }


    // ============================
    // GET ALL TRANSACTIONS
    // ============================
    @GetMapping
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ============================
    // GET TRANSACTION BY ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id
    ) {
        return transactionRepository.findById(id)
                .map(transaction -> ResponseEntity.ok(toResponse(transaction)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ============================
    // ENTITY → DTO MAPPER
    // ============================
    private TransactionResponse toResponse(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId());
        r.setAccountNumber(t.getAccountNumber());
        r.setAmount(t.getAmount());
        r.setCurrency(t.getCurrency());
        r.setTransactionType(t.getTransactionType());
        r.setRiskLevel(t.getRiskLevel());
        r.setFraudScore(t.getFraudScore());
        r.setFraud(t.getIsFraud());
        r.setApprovalStatus(t.getApprovalStatus());
        r.setTransactionTime(t.getTransactionTime());
        r.setFraudReason(t.getFraudReason());
        return r;
    }
}
