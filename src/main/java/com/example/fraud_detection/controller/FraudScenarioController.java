package com.example.fraud_detection.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fraud scenario APIs are disabled.
 * Scenarios run internally during transaction processing.
 */
@RestController
@Profile("dev")
public class FraudScenarioController {
    // intentionally empty
}
