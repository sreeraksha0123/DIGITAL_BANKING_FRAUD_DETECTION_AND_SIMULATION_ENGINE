package com.example.fraud_detection.repository;

import com.example.fraud_detection.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // =========================
    // BASIC COUNTS
    // =========================

    long countByIsFraudTrue();

    long countByApprovalStatus(String approvalStatus);

    // =========================
    // AGGREGATES
    // =========================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.approvalStatus = 'BLOCKED'
    """)
    Double getTotalBlockedAmount();

    @Query("""
        SELECT COALESCE(AVG(t.fraudScore), 0)
        FROM Transaction t
        WHERE t.fraudScore IS NOT NULL
    """)
    Double getAverageFraudScore();

    // =========================
    // DASHBOARD HELPERS
    // =========================

    @Query("""
        SELECT COUNT(t)
        FROM Transaction t
        WHERE t.riskLevel = 'HIGH'
    """)
    long countHighRiskTransactions();
}
