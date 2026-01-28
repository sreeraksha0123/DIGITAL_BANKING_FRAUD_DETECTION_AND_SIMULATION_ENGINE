package com.example.fraud_detection.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    private String accountNumber;
    private Double amount;
    private String currency;
    private String transactionType;

    private String merchantId;
    private String deviceId;
    private String ipAddress;

    private String location;
    private String city;
    private String country;

    private LocalDateTime transactionTime;
    private Boolean successStatus;
}
