package org.example.controller;

import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    // Constructor injection
    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // POST /api/transactions  -> validate + apply fraud rules + save to DB
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction tx) {

        // --------- BASIC VALIDATION ---------
        if (tx.getAmount() == null || tx.getAmount() <= 0) {
            return ResponseEntity.badRequest().body("Amount must be > 0");
        }

        if (tx.getCurrency() == null ||
                !(tx.getCurrency().equalsIgnoreCase("INR") || tx.getCurrency().equalsIgnoreCase("USD"))) {
            return ResponseEntity.badRequest().body("Currency must be INR or USD");
        }

        if (tx.getTransactionType() == null ||
                !(tx.getTransactionType().equalsIgnoreCase("UPI")
                        || tx.getTransactionType().equalsIgnoreCase("CARD"))) {
            return ResponseEntity.badRequest().body("Transaction type must be UPI or CARD");
        }

        // Default successStatus to true if null
        if (tx.getSuccessStatus() == null) {
            tx.setSuccessStatus(true);
        }

        // Auto-generate transactionId if missing
        if (tx.getTransactionId() == null || tx.getTransactionId().isEmpty()) {
            tx.setTransactionId(java.util.UUID.randomUUID().toString());
        }

        // Auto-set timestamp if missing
        if (tx.getTimestamp() == null || tx.getTimestamp().isEmpty()) {
            tx.setTimestamp(java.time.LocalDateTime.now().toString());
        }

        // --------- FRAUD / SUSPICIOUS RULES ---------
        boolean suspicious = false;
        StringBuilder reason = new StringBuilder();

        // Rule 1: Very high INR amount
        if ("INR".equalsIgnoreCase(tx.getCurrency())
                && tx.getAmount() != null
                && tx.getAmount() > 80000) {
            suspicious = true;
            reason.append("High INR amount (> 80000). ");
        }

        // Rule 2: Currency-location mismatch (INR in New York)
        if ("INR".equalsIgnoreCase(tx.getCurrency())
                && tx.getLocation() != null
                && tx.getLocation().toLowerCase().contains("new york")) {
            suspicious = true;
            reason.append("INR transaction from New York location. ");
        }

        // Rule 3: CARD used via MOBILE channel (a bit odd)
        if (tx.getTransactionType() != null
                && tx.getChannel() != null
                && tx.getTransactionType().equalsIgnoreCase("CARD")
                && tx.getChannel().equalsIgnoreCase("MOBILE")) {
            suspicious = true;
            reason.append("CARD payment initiated via MOBILE channel. ");
        }

        // Apply fraud decision
        if (suspicious) {
            tx.setSuccessStatus(false);                 // mark as blocked / suspicious
            tx.setFraudReason(reason.toString());       // include reason in JSON response
            System.out.println("[FRAUD FLAGGED] Txn " + tx.getTransactionId() + " - " + reason);
        } else {
            tx.setFraudReason("NO_FRAUD_DETECTED");
        }

        // --------- SAVE TO DB ---------
        Transaction saved = transactionRepository.save(tx);

        // Return saved object as JSON (with fraudReason field)
        return ResponseEntity.ok(saved);
    }

    // GET /api/transactions  -> list all from DB
    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    // GET /api/transactions/generate -> create 1 random transaction, save, return it
    @GetMapping("/generate")
    public ResponseEntity<?> generateFakeTransaction() {
        Transaction tx = new Transaction();

        // Generate random but valid data
        double amount = 1000 + Math.random() * 9000; // between 1000 and 10000
        tx.setAmount(amount);

        String currency = Math.random() > 0.5 ? "INR" : "USD";
        tx.setCurrency(currency);

        tx.setSenderAccount("ACC" + (int)(Math.random() * 1_000_000));
        tx.setReceiverAccount("ACC" + (int)(Math.random() * 1_000_000));

        String txnType = Math.random() > 0.5 ? "UPI" : "CARD";
        tx.setTransactionType(txnType);

        String channel = Math.random() > 0.5 ? "MOBILE" : "WEB";
        tx.setChannel(channel);

        tx.setDeviceId("DEV-" + (int)(Math.random() * 999999));

        String location = Math.random() > 0.5 ? "Chennai, India" : "New York, USA";
        tx.setLocation(location);

        String ip = (int)(Math.random()*255) + "." +
                (int)(Math.random()*255) + "." +
                (int)(Math.random()*255) + "." +
                (int)(Math.random()*255);
        tx.setIpAddress(ip);

        boolean success = Math.random() > 0.2; // 80% success
        tx.setSuccessStatus(success);

        // Always set transactionId and timestamp
        tx.setTransactionId(java.util.UUID.randomUUID().toString());
        tx.setTimestamp(java.time.LocalDateTime.now().toString());

        // We reuse the same fraud rules by calling createTransaction,
        // but since it's an internal call, we just let repository save:
        // (simpler: directly save)
        Transaction saved = transactionRepository.save(tx);

        return ResponseEntity.ok(saved);
    }
}
