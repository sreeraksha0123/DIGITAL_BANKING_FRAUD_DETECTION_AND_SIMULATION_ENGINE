package com.example.fraud_detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.example.fraud_detection.config",
        "com.example.fraud_detection.controller",
        "com.example.fraud_detection.service",
        "com.example.fraud_detection.repository",
        "com.example.fraud_detection.entity"
})
public class DigitalBankingFraudDetectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingFraudDetectionApplication.class, args);
    }
}
