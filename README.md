# DIGITAL_BANKING_FRAUD_DETECTION_AND_SIMULATION_ENGINE

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![React](https://img.shields.io/badge/React-18-61DAFB.svg)](https://reactjs.org/)
[![Machine Learning](https://img.shields.io/badge/ML-Hybrid%20Detection-FF6F61.svg)](https://scikit-learn.org/)
![License](https://img.shields.io/badge/License-MIT-green.svg)

---

## 🌟 Project Overview

This project implements a **real-time digital banking fraud detection system** using a **hybrid approach**:

* **Rule-based fraud detection** for deterministic, explainable fraud patterns
* **Machine learning–based fraud scoring** for probabilistic and behavioral fraud detection

The system is composed of:

* **React frontend** for fraud analysts
* **Spring Boot backend** as the fraud decision engine
* **Python (Flask) ML microservice** for fraud probability prediction
* **MySQL database** for transactional and audit data

---

## 🧱 System Architecture (Detailed & Real)

```mermaid
graph LR
    UI[React Dashboard]
    TC[TransactionController]
    TS[TransactionService]
    FDS[FraudDetectionService]
    RS[RiskScoringService]
    ABS[AccountBlockService]
    ALS[AlertService]
    MLS[MlFraudScoringService]
    DB[(MySQL Database)]

    UI -->|REST| TC
    TC --> TS
    TS --> FDS

    FDS -->|Rule Checks| RS
    FDS -->|ML Request| MLS
    MLS -->|Fraud Probability| FDS

    RS -->|Final Score| TS
    TS -->|Block if High Risk| ABS
    TS -->|Create Alert| ALS

    TS --> DB
    ABS --> DB
    ALS --> UI
```
---

## 🔄 End-to-End Transaction Flow (Code-Level)

```mermaid
sequenceDiagram
    participant UI as React Dashboard
    participant TC as TransactionController
    participant TS as TransactionService
    participant FD as FraudDetectionService
    participant ML as ML Service (Flask)
    participant RS as RiskScoringService
    participant DB as MySQL
    participant AL as AlertService

    UI->>TC: POST /transactions
    TC->>TS: createTransaction(dto)

    TS->>FD: analyzeTransaction(entity)

    FD->>FD: Apply Rule-Based Checks
    FD->>ML: Send Features
    ML-->>FD: Fraud Probability

    FD->>RS: calculateFinalScore(ruleScore, mlScore)
    RS-->>TS: Risk Level

    TS->>DB: Save Transaction
    alt HIGH RISK
        TS->>AL: triggerAlert()
        AL-->>UI: WebSocket Notification
    end

    TS-->>UI: TransactionResponseDTO
```

---

## 📂 Backend Folder Structure (Exact)

```
com.bank.fraud
│
├── controller
│   ├── TransactionController.java
│   ├── AnalyticsController.java
│   └── NotificationController.java
│
├── service
│   ├── TransactionService.java
│   ├── FraudDetectionService.java
│   ├── RiskScoringService.java
│   ├── AccountBlockService.java
│   ├── AlertService.java
│   └── AnalyticsService.java
│
├── repository
│   ├── TransactionRepository.java
│   ├── UserRepository.java
│   ├── BlockedAccountRepository.java
│   └── AuditLogRepository.java
│
├── model
│   ├── Transaction.java
│   ├── User.java
│   ├── BlockedAccount.java
│   └── AuditLog.java
│
├── dto
│   ├── TransactionRequestDTO.java
│   ├── TransactionResponseDTO.java
│   ├── FraudScoreDTO.java
│   └── AnalyticsDTO.java
│
├── ml
│   ├── MlFraudScoringService.java
│   └── ModelFeatureMapper.java
│
├── config
│   ├── SecurityConfig.java
│   ├── JwtAuthFilter.java
│   └── WebSocketConfig.java
│
└── DigitalBankingFraudDetectionApplication.java
```

---

## 🗄️ Database Schema (NO MISSING FIELDS)

```mermaid
erDiagram
    TRANSACTIONS {
        BIGINT id PK
        VARCHAR account_number
        VARCHAR transaction_type
        DECIMAL amount
        VARCHAR location
        VARCHAR city
        VARCHAR country
        INT fraud_score
        VARCHAR risk_level
        BOOLEAN is_fraud
        VARCHAR approval_status
        VARCHAR device_id
        VARCHAR ip_address
        TIMESTAMP transaction_time
        TIMESTAMP created_at
    }

    BLOCKED_ACCOUNTS {
        BIGINT id PK
        VARCHAR account_number
        VARCHAR block_reason
        TIMESTAMP blocked_at
    }

    AUDIT_LOGS {
        BIGINT id PK
        VARCHAR action_type
        TEXT description
        TIMESTAMP action_time
    }

    USERS {
        BIGINT id PK
        VARCHAR username
        VARCHAR password
        VARCHAR role
        BOOLEAN enabled
        TIMESTAMP created_at
    }

    USERS ||--o{ AUDIT_LOGS : performs
```
---

## 🤖 Fraud Detection Logic (Exact)

```mermaid
graph TD
    T[Transaction Input]
    R[Rule-Based Engine]
    M[ML Fraud Scoring]
    S[Risk Scoring Service]
    D{Decision}

    T --> R
    T --> M
    R --> S
    M --> S
    S --> D

    D -->|LOW| A[Approved]
    D -->|MEDIUM| B[Manual Review]
    D -->|HIGH| C[Blocked + Alert]
```

### Rule Examples

* Amount threshold
* Velocity check
* Late-night transactions
* New location / device

### ML

* Logistic Regression - Classification with probability
* Output: fraud probability (0–100)

---

## 🖥️ Frontend Dashboard (What You Actually Built)

```mermaid
graph TD
    H[Dashboard]
    T[Live Transactions Table]
    D[Details View]
    A[Add Transaction]
    N[Notifications]
    C[Charts & Metrics]

    H --> T
    T --> D
    H --> A
    H --> N
    H --> C
```

Features:

* Risk color coding
* Expandable details
* Auto & manual refresh
* CSV export
* Real-time alerts

---

## ⚡ Performance & 🔐 Security (Realistic)

### Performance

| Area       | Implementation        |
| ---------- | --------------------- |
| APIs       | REST                  |
| DB         | Indexed queries       |
| Processing | Async services        |
| ML         | External microservice |
| UI         | WebSockets            |

### Security

| Area  | Implementation   |
| ----- | ---------------- |
| Auth  | JWT              |
| Roles | Analyst / Admin  |
| Data  | HTTPS            |
| Audit | AuditLog table   |
| Fraud | Account blocking |

---

## 🚀 Running the Project

```bash
# Backend
mvn spring-boot:run

# ML service
python app.py

# Frontend
npm install
npm start
```

---

## 👨‍💻 Author

**Sree Raksha S P**
GitHub: [https://github.com/sreeraksha0123](https://github.com/sreeraksha0123)
LinkedIn: [https://www.linkedin.com/in/sreeraksha0123](https://www.linkedin.com/in/sreeraksha0123)

---

### 🔒 Secure Banking • ⚡ Real-Time Detection • 📊 Explainable Decisions

