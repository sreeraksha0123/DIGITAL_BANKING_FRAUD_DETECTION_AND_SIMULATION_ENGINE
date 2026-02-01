package com.bank.fraud.repository;

import com.bank.fraud.model.BlockedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface BlockedAccountRepository extends JpaRepository<BlockedAccount, Long> {

    // ---------- BASIC LOOKUPS ----------

    Optional<BlockedAccount> findByAccountId(String accountId);

    boolean existsByAccountIdAndActiveBlockTrue(String accountId);

    // ---------- BLOCK MANAGEMENT ----------

    @Query("""
        SELECT b
        FROM BlockedAccount b
        WHERE b.activeBlock = true
          AND b.blockedUntil <= :currentTime
    """)
    List<BlockedAccount> findExpiredBlocks(
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query("""
        SELECT b
        FROM BlockedAccount b
        WHERE b.activeBlock = true
    """)
    List<BlockedAccount> findAllActiveBlockedAccounts();
}
