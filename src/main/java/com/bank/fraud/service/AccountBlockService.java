package com.bank.fraud.service;

import com.bank.fraud.model.BlockedAccount;
import com.bank.fraud.repository.BlockedAccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountBlockService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_HOURS = 24;

    private final BlockedAccountRepository blockedAccountRepository;
    private final AuditLogService auditLogService;

    public AccountBlockService(
            BlockedAccountRepository blockedAccountRepository,
            AuditLogService auditLogService
    ) {
        this.blockedAccountRepository = blockedAccountRepository;
        this.auditLogService = auditLogService;
    }

    // --------------------------------------------------
    // CHECK IF ACCOUNT IS BLOCKED
    // --------------------------------------------------

    public boolean isAccountBlocked(String accountId) {
        return blockedAccountRepository
                .existsByAccountIdAndActiveBlockTrue(accountId);
    }

    // --------------------------------------------------
    // BLOCK ACCOUNT AFTER FRAUD DETECTION
    // --------------------------------------------------

    @Transactional
    public void blockAccount(String accountId, List<String> reasons) {

        BlockedAccount blockedAccount = blockedAccountRepository
                .findByAccountId(accountId)
                .orElseGet(() -> createNewBlockedAccount(accountId));

        blockedAccount.setFailedAttempts(blockedAccount.getFailedAttempts() + 1);

        // Check threshold within time window
        if (blockedAccount.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            blockedAccount.setActiveBlock(true);
            blockedAccount.setBlockedUntil(
                    LocalDateTime.now().plusHours(BLOCK_DURATION_HOURS)
            );
            blockedAccount.setBlockReason(String.join(", ", reasons));

            auditLogService.logAccountBlocked(accountId, blockedAccount.getBlockReason());
        }

        blockedAccountRepository.save(blockedAccount);
    }

    // --------------------------------------------------
    // AUTO UNBLOCK EXPIRED ACCOUNTS
    // --------------------------------------------------

    @Transactional
    public void unblockExpiredAccounts() {

        List<BlockedAccount> expiredBlocks =
                blockedAccountRepository.findExpiredBlocks(LocalDateTime.now());

        for (BlockedAccount blockedAccount : expiredBlocks) {
            blockedAccount.setActiveBlock(false);
            blockedAccount.setFailedAttempts(0);
            blockedAccount.setBlockReason("Auto-unblocked after timeout");

            blockedAccountRepository.save(blockedAccount);

            auditLogService.logAccountUnblocked(blockedAccount.getAccountId());
        }
    }

    // --------------------------------------------------
    // HELPER METHOD
    // --------------------------------------------------

    private BlockedAccount createNewBlockedAccount(String accountId) {
        BlockedAccount account = new BlockedAccount();
        account.setAccountId(accountId);
        account.setFailedAttempts(0);
        account.setFirstFailedAttemptTime(LocalDateTime.now());
        account.setActiveBlock(false);
        account.setBlockedUntil(LocalDateTime.now());
        account.setBlockReason("Initial monitoring");

        return account;
    }
}
