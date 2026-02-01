package com.bank.fraud.repository;

import com.bank.fraud.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // ---------- BASIC AUDIT LOOKUPS ----------

    List<AuditLog> findByEntityType(String entityType);

    List<AuditLog> findByEntityId(String entityId);

    List<AuditLog> findByAction(String action);

    // ---------- TIME-BASED AUDIT QUERIES ----------

    @Query("""
        SELECT a
        FROM AuditLog a
        WHERE a.eventTime BETWEEN :startTime AND :endTime
        ORDER BY a.eventTime DESC
    """)
    List<AuditLog> findAuditLogsBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // ---------- SECURITY & COMPLIANCE ----------

    @Query("""
        SELECT a
        FROM AuditLog a
        WHERE a.performedBy = :performedBy
        ORDER BY a.eventTime DESC
    """)
    List<AuditLog> findByPerformedBy(
            @Param("performedBy") String performedBy
    );
}
