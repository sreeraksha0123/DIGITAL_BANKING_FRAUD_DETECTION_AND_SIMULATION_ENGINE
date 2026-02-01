package com.bank.fraud.controller;

import com.bank.fraud.dto.AnalyticsDTO;
import com.bank.fraud.service.AnalyticsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // --------------------------------------------------
    // DASHBOARD ANALYTICS API
    // --------------------------------------------------

    @GetMapping
    public ResponseEntity<AnalyticsDTO> getAnalytics() {
        AnalyticsDTO analytics = analyticsService.getSystemAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // --------------------------------------------------
    // HEALTH CHECK (OPTIONAL)
    // --------------------------------------------------

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Analytics service is up");
    }
}
