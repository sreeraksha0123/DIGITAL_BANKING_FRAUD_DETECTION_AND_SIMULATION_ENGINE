package com.example.fraud_detection.controller;

import com.example.fraud_detection.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
@CrossOrigin(origins = "http://localhost:3000")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    // ✅ Already working
    @GetMapping("/summary")
    public Map<String, Object> getSystemSummary() {
        return metricsService.getSystemSummary();
    }

    // ✅ Fraud rate analytics
    @GetMapping("/fraud-rate")
    public Map<String, Object> getFraudRate() {
        return metricsService.calculateFraudRate();
    }

    // ✅ Rule breakdown (THIS FIXES "Loading rule breakdown...")
    @GetMapping("/rule-breakdown")
    public Map<String, Object> getRuleBreakdown() {
        return metricsService.getRuleDetectionBreakdown();
    }

    // ✅ Risk distribution (already working)
    @GetMapping("/risk-distribution")
    public Map<String, Object> getRiskDistribution() {
        return metricsService.getRiskLevelDistribution();
    }

    // ✅ Time-based analysis
    @GetMapping("/time-analysis")
    public Map<String, Object> getTimeAnalysis(
            @RequestParam(defaultValue = "7days") String period) {
        return metricsService.getTimeBasedAnalysis(period);
    }

    // ✅ System effectiveness (THIS FIXES "Loading effectiveness metrics...")
    @GetMapping("/effectiveness")
    public Map<String, Object> getSystemEffectiveness() {
        return metricsService.calculateSystemEffectiveness();
    }

    // ✅ Performance metrics (THIS FIXES "Loading performance metrics...")
    @GetMapping("/performance")
    public Map<String, Object> getPerformanceMetrics() {
        return metricsService.getPerformanceMetrics();
    }
}
