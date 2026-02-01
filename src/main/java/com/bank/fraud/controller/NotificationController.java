package com.bank.fraud.controller;

import com.bank.fraud.model.AuditLog;
import com.bank.fraud.repository.AuditLogRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final AuditLogRepository auditLogRepository;

    public NotificationController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // --------------------------------------------------
    // FETCH RECENT FRAUD / SECURITY NOTIFICATIONS
    // --------------------------------------------------

    @GetMapping
    public ResponseEntity<List<AuditLog>> getRecentNotifications() {

        // Fetch recent security-related audit logs
        List<AuditLog> notifications =
                auditLogRepository.findByAction("BLOCKED");

        return ResponseEntity.ok(notifications);
    }

    // --------------------------------------------------
    // HEALTH CHECK (OPTIONAL)
    // --------------------------------------------------

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notification service is up");
    }
}
