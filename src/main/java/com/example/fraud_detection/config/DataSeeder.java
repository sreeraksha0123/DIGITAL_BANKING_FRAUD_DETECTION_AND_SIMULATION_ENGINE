package com.example.fraud_detection.config;

import com.example.fraud_detection.dto.TransactionRequest;
import com.example.fraud_detection.service.AdvancedFraudDetectionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Deterministic dataset seeder for a stable, realistic dashboard.
 *
 * Guarantees:
 *  - 70% LOW risk
 *  - 20% MEDIUM risk
 *  - 10% HIGH risk
 *
 * NO randomness.
 * Frontend metrics will ALWAYS look correct.
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final AdvancedFraudDetectionService fraudService;

    private static final int LOW = 700;
    private static final int MEDIUM = 200;
    private static final int HIGH = 100;

    @PostConstruct
    public void seed() {
        seedLowRisk();
        seedMediumRisk();
        seedHighRisk();

        System.out.println("✅ Deterministic fraud dataset seeded");
    }

    // =====================================================
    // LOW RISK — clean traffic (GREEN)
    // =====================================================
    private void seedLowRisk() {
        for (int i = 1; i <= LOW; i++) {

            TransactionRequest tx = new TransactionRequest();
            tx.setAccountNumber("ACC_LOW_" + (i % 50)); // reuse accounts → stable velocity
            tx.setAmount(300.0 + i);                    // always < 5k
            tx.setTransactionType("CARD");
            tx.setCurrency("USD");
            tx.setCountry("USA");
            tx.setCity("New York");
            tx.setTransactionTime(dayTime(i));

            fraudService.processTransaction(tx);
        }
    }

    // =====================================================
    // MEDIUM RISK — warnings & review (YELLOW)
    // =====================================================
    private void seedMediumRisk() {
        for (int i = 1; i <= MEDIUM; i++) {

            TransactionRequest tx = new TransactionRequest();
            tx.setAccountNumber("ACC_MED_" + (i % 20)); // tighter reuse → velocity ↑
            tx.setAmount(20000.0 + (i * 50));           // 20k–30k
            tx.setTransactionType("TRANSFER");
            tx.setCurrency("USD");
            tx.setCountry("Brazil");
            tx.setCity("São Paulo");

            // Half night, half day → deterministic mix
            tx.setTransactionTime(i % 2 == 0 ? nightTime(i) : dayTime(i));

            fraudService.processTransaction(tx);
        }
    }

    // =====================================================
    // HIGH RISK — fraud & blocked (RED)
    // =====================================================
    private void seedHighRisk() {
        for (int i = 1; i <= HIGH; i++) {

            TransactionRequest tx = new TransactionRequest();
            tx.setAccountNumber("ACC_HIGH_" + (i % 5)); // heavy reuse → high velocity
            tx.setAmount(120000.0 + (i * 1000));        // very high amount
            tx.setTransactionType("INTERNATIONAL");
            tx.setCurrency("USD");
            tx.setCountry("Nigeria");
            tx.setCity("Lagos");
            tx.setTransactionTime(nightTime(i));        // always night

            fraudService.processTransaction(tx);
        }
    }

    // =====================================================
    // TIME HELPERS (DETERMINISTIC)
    // =====================================================
    private LocalDateTime dayTime(int offset) {
        return LocalDateTime.now()
                .minusDays(2)
                .withHour(13)
                .withMinute(offset % 60)
                .withSecond(0);
    }

    private LocalDateTime nightTime(int offset) {
        return LocalDateTime.now()
                .minusDays(2)
                .withHour(2)
                .withMinute(offset % 60)
                .withSecond(0);
    }
}
