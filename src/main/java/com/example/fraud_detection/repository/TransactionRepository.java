package com.example.fraud_detection.repository;

import com.example.fraud_detection.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByIsFraudTrue();

    List<Transaction> findByRiskLevel(String riskLevel);

    List<Transaction> findByAccountNumber(String accountNumber);

    @Query("SELECT t FROM Transaction t WHERE t.accountNumber = :accountNumber AND t.transactionTime > :time")
    List<Transaction> findByAccountNumberAndTransactionTimeAfter(
            @Param("accountNumber") String accountNumber,
            @Param("time") LocalDateTime time);

    List<Transaction> findByUserId(Integer userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.isFraud = true")
    long countByIsFraudTrue();

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.riskLevel = :riskLevel")
    long countByRiskLevel(@Param("riskLevel") String riskLevel);

    @Query("SELECT AVG(t.fraudScore) FROM Transaction t WHERE t.fraudScore IS NOT NULL")
    Double findAverageFraudScore();

    @Query("SELECT t FROM Transaction t WHERE t.fraudScore > :minScore ORDER BY t.fraudScore DESC")
    List<Transaction> findHighScoreTransactions(@Param("minScore") int minScore);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionTime BETWEEN :start AND :end")
    long countTransactionsBetween(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    List<Transaction> findByCountry(String country);

    @Query("SELECT t FROM Transaction t WHERE t.isFraud = false AND t.riskLevel = 'LOW' ORDER BY t.transactionTime DESC")
    List<Transaction> findLowRiskTransactions();

    boolean existsById(Long id);
}