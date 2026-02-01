package com.bank.fraud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "blocked_accounts",
        indexes = {
                @Index(name = "idx_blocked_account_id", columnList = "accountId"),
                @Index(name = "idx_block_until", columnList = "blockedUntil")
        }
)
public class BlockedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    private String accountId;

    @Column(nullable = false)
    private Integer failedAttempts;

    @Column(nullable = false)
    private LocalDateTime firstFailedAttemptTime;

    @Column(nullable = false)
    private LocalDateTime blockedUntil;

    @Column(nullable = false)
    private Boolean activeBlock;

    @Column(nullable = false, length = 100)
    private String blockReason;

    // ---------- Constructors ----------

    public BlockedAccount() {
        // Required by JPA
    }

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getFirstFailedAttemptTime() {
        return firstFailedAttemptTime;
    }

    public void setFirstFailedAttemptTime(LocalDateTime firstFailedAttemptTime) {
        this.firstFailedAttemptTime = firstFailedAttemptTime;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public Boolean getActiveBlock() {
        return activeBlock;
    }

    public void setActiveBlock(Boolean activeBlock) {
        this.activeBlock = activeBlock;
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
    }
}
