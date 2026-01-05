package com.example.fraud_detection.controller;

import com.example.fraud_detection.service.FraudScenarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/scenarios")
@CrossOrigin(origins = "http://localhost:3000")
public class FraudScenarioController {

    private final FraudScenarioService fraudScenarioService;

    public FraudScenarioController(FraudScenarioService fraudScenarioService) {
        this.fraudScenarioService = fraudScenarioService;
    }

    @PostMapping("/high-value")
    public ResponseEntity<Map<String, Object>> highValue() {
        return ResponseEntity.ok(fraudScenarioService.testHighValueTransaction());
    }

    @PostMapping("/rapid-transactions")
    public ResponseEntity<Map<String, Object>> rapidTransactions() {
        return ResponseEntity.ok(fraudScenarioService.testRapidTransactions());
    }

    @PostMapping("/location-mismatch")
    public ResponseEntity<Map<String, Object>> locationMismatch() {
        return ResponseEntity.ok(fraudScenarioService.testLocationMismatch());
    }

    @PostMapping("/suspicious-merchant")
    public ResponseEntity<Map<String, Object>> suspiciousMerchant() {
        return ResponseEntity.ok(fraudScenarioService.testSuspiciousMerchant());
    }

    @PostMapping("/odd-hours")
    public ResponseEntity<Map<String, Object>> oddHours() {
        return ResponseEntity.ok(fraudScenarioService.testOddHoursTransaction());
    }

    @PostMapping("/run-all")
    public Map<String, Object> runAll() {

        List<Map<String, Object>> scenarios =
                fraudScenarioService.runAllScenarios();

        long passed = scenarios.stream()
                .filter(s -> Boolean.TRUE.equals(s.get("testPassed")))
                .count();

        int total = scenarios.size();
        long failed = total - passed;

        double successRate = total == 0
                ? 0
                : (passed * 100.0) / total;

        String overallStatus =
                passed == total ? "ALL_PASSED"
                        : passed == 0 ? "ALL_FAILED"
                        : "PARTIAL_PASS";

        Map<String, Object> response = new HashMap<>();
        response.put("totalScenarios", total);
        response.put("passed", passed);
        response.put("failed", failed);
        response.put("successRate", successRate);
        response.put("overallStatus", overallStatus);
        response.put("scenarios", scenarios);

        return response;
    }


}
