package com.example.fraud_detection.repository;

import com.example.fraud_detection.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /* ================= BASIC QUERIES ================= */

    List<Transaction> findByIsFraudTrue();

    long countByIsFraudTrue();

    List<Transaction> findByRiskLevel(String riskLevel);

    long countByRiskLevel(String riskLevel);

    List<Transaction> findByAccountNumber(String accountNumber);

    List<Transaction> findByUserId(Integer userId);

    List<Transaction> findByTransactionTimeAfter(LocalDateTime time);

    List<Transaction> findByAccountNumberAndTransactionTimeAfter(
            String accountNumber,
            LocalDateTime time
    );

    // ✅ REQUIRED for FraudScenarioService (latest transaction)
    Transaction findTopByAccountNumberOrderByTransactionTimeDesc(String accountNumber);

    /* ================= ANALYTICS QUERIES ================= */

    @Query("SELECT COALESCE(AVG(t.fraudScore), 0) FROM Transaction t")
    Double findAverageFraudScore();

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.approvalStatus = 'BLOCKED'
           """)
    Double sumBlockedAmount();

    @Query("SELECT MIN(t.transactionTime) FROM Transaction t")
    LocalDateTime findOldestTransactionDate();
}
