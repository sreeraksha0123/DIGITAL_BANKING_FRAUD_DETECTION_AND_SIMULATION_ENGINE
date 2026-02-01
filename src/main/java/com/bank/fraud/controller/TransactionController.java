package com.bank.fraud.controller;

import com.bank.fraud.dto.TransactionRequestDTO;
import com.bank.fraud.dto.TransactionResponseDTO;
import com.bank.fraud.service.TransactionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // --------------------------------------------------
    // CREATE / PROCESS TRANSACTION
    // --------------------------------------------------

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO request
    ) {
        TransactionResponseDTO response =
                transactionService.processTransaction(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // --------------------------------------------------
    // HEALTH CHECK (OPTIONAL)
    // --------------------------------------------------

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Transaction service is up");
    }
}
