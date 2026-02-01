package com.bank.fraud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_entity", columnList = "entityType"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_time", columnList = "eventTime")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String entityType;
    // TRANSACTION, ACCOUNT, USER, RULE

    @Column(nullable = false, length = 30)
    private String entityId;
    // transactionId / accountId / userId

    @Column(nullable = false, length = 50)
    private String action;
    // CREATED, BLOCKED, UNBLOCKED, UPDATED, DELETED

    @Column(nullable = false, length = 50)
    private String performedBy;
    // SYSTEM / ADMIN / ANALYST

    @Column(nullable = false, length = 255)
    private String description;
    // Human-readable explanation

    @Column(nullable = false)
    private LocalDateTime eventTime;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    // ---------- Constructors ----------

    public AuditLog() {
        // Required by JPA
    }

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
