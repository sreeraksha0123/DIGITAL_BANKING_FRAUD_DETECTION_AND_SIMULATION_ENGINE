package com.bank.fraud.repository;

import com.bank.fraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ---------- BASIC CRUD & LOOKUPS ----------

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findByAccountId(String accountId);

    List<Transaction> findByStatus(String status);

    List<Transaction> findByRiskLevel(String riskLevel);

    // ---------- FRAUD DETECTION SUPPORT ----------

    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.accountId = :accountId
          AND t.transactionTime >= :fromTime
    """)
    long countRecentTransactions(
            @Param("accountId") String accountId,
            @Param("fromTime") LocalDateTime fromTime
    );

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.fraudDetected = true
        ORDER BY t.transactionTime DESC
    """)
    List<Transaction> findFraudTransactions();

    // ---------- DASHBOARD & ANALYTICS ----------

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.transactionTime BETWEEN :start AND :end
        ORDER BY t.transactionTime DESC
    """)
    List<Transaction> findTransactionsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.riskLevel = :riskLevel
    """)
    long countByRiskLevel(@Param("riskLevel") String riskLevel);

    // ---------- DUPLICATE PREVENTION ----------

    boolean existsByTransactionId(String transactionId);
}
