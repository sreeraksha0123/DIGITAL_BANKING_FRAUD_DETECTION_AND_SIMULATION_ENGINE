package com.bank.fraud.service;

import com.bank.fraud.model.AuditLog;
import com.bank.fraud.model.Transaction;
import com.bank.fraud.repository.AuditLogRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // --------------------------------------------------
    // TRANSACTION AUDIT LOG
    // --------------------------------------------------

    public void logTransactionEvent(Transaction transaction) {

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("TRANSACTION");
        auditLog.setEntityId(transaction.getTransactionId());
        auditLog.setAction(transaction.getStatus());
        auditLog.setPerformedBy("SYSTEM");
        auditLog.setDescription(
                "Transaction processed with risk level: "
                        + transaction.getRiskLevel()
                        + ", fraud detected: "
                        + transaction.getFraudDetected()
        );
        auditLog.setEventTime(LocalDateTime.now());
        auditLog.setIpAddress(transaction.getIpAddress());

        auditLogRepository.save(auditLog);
    }

    // --------------------------------------------------
    // ACCOUNT BLOCK AUDIT LOG
    // --------------------------------------------------

    public void logAccountBlocked(String accountId, String reason) {

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("ACCOUNT");
        auditLog.setEntityId(accountId);
        auditLog.setAction("BLOCKED");
        auditLog.setPerformedBy("SYSTEM");
        auditLog.setDescription("Account blocked due to: " + reason);
        auditLog.setEventTime(LocalDateTime.now());
        auditLog.setIpAddress("SYSTEM");

        auditLogRepository.save(auditLog);
    }

    // --------------------------------------------------
    // ACCOUNT UNBLOCK AUDIT LOG
    // --------------------------------------------------

    public void logAccountUnblocked(String accountId) {

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType("ACCOUNT");
        auditLog.setEntityId(accountId);
        auditLog.setAction("UNBLOCKED");
        auditLog.setPerformedBy("SYSTEM");
        auditLog.setDescription("Account automatically unblocked after timeout");
        auditLog.setEventTime(LocalDateTime.now());
        auditLog.setIpAddress("SYSTEM");

        auditLogRepository.save(auditLog);
    }

    // --------------------------------------------------
    // ADMIN / USER ACTION AUDIT LOG
    // --------------------------------------------------

    public void logAdminAction(
            String entityType,
            String entityId,
            String action,
            String performedBy,
            String description,
            String ipAddress
    ) {

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setDescription(description);
        auditLog.setEventTime(LocalDateTime.now());
        auditLog.setIpAddress(ipAddress);

        auditLogRepository.save(auditLog);
    }
}
