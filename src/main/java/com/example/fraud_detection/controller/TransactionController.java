package com.example.fraud_detection.controller;

import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.dto.TransactionResponse;
import com.example.fraud_detection.entity.Transaction;
import com.example.fraud_detection.repository.TransactionRepository;
import com.example.fraud_detection.service.AdvancedFraudDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/transactions")
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

    // ================= CREATE TRANSACTION =================
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionRequest request
    ) {
        TransactionResponse response = fraudService.processTransaction(request);
        return ResponseEntity.ok(response);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return transactionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= BY ACCOUNT =================
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getByAccount(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(
                transactionRepository.findByAccountNumber(accountNumber)
        );
    }

    // ================= FRAUD ONLY =================
    @GetMapping("/fraud")
    public ResponseEntity<List<Transaction>> fraudOnly() {
        return ResponseEntity.ok(
                transactionRepository.findByIsFraudTrue()
        );
    }

    // ================= HIGH RISK =================
    @GetMapping("/risk/high")
    public ResponseEntity<List<Transaction>> highRisk() {
        return ResponseEntity.ok(
                transactionRepository.findByRiskLevel("HIGH")
        );
    }

    // ================= MEDIUM RISK =================
    @GetMapping("/risk/medium")
    public ResponseEntity<List<Transaction>> mediumRisk() {
        return ResponseEntity.ok(
                transactionRepository.findByRiskLevel("MEDIUM")
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!transactionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
