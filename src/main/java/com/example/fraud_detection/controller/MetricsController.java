package com.example.fraud_detection.controller;

import com.example.fraud_detection.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
@CrossOrigin
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/summary")
    public Map<String, Object> getDashboardMetrics() {
        return metricsService.getDashboardMetrics();
    }
}
