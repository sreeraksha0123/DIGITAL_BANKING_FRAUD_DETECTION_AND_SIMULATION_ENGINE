package com.bank.fraud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class TransactionRequestDTO {

    @NotBlank(message = "Transaction ID is required")
    @Size(max = 30)
    private String transactionId;

    @NotBlank(message = "Account ID is required")
    @Size(max = 20)
    private String accountId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 50)
    private String customerName;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;   // UPI, CARD, NET_BANKING, WALLET

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "IP address is required")
    private String ipAddress;

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    // ---------- Constructors ----------

    public TransactionRequestDTO() {
    }

    // ---------- Getters & Setters ----------

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
