import pandas as pd
from sklearn.linear_model import LogisticRegression
import joblib

# --------------------------------------------------
# DUMMY TRAINING DATA (COLLEGE / DEMO PURPOSE)
# --------------------------------------------------
# Features MUST match inference-time features exactly

data = {
    "amount": [
        100, 5000, 200000, 50, 120000, 300, 90000, 40,
        75000, 150000, 600, 25000
    ],
    "velocity": [
        1, 2, 15, 1, 12, 1, 10, 1,
        8, 14, 1, 4
    ],
    "night_txn": [
        0, 0, 1, 0, 1, 0, 1, 0,
        1, 1, 0, 0
    ],
    "is_card": [
        0, 1, 1, 0, 1, 0, 1, 0,
        1, 1, 0, 0
    ],
    "is_wallet": [
        0, 0, 0, 0, 0, 1, 0, 0,
        0, 0, 1, 0
    ],
    "fraud": [
        0, 0, 1, 0, 1, 0, 1, 0,
        1, 1, 0, 0
    ]
}

df = pd.DataFrame(data)

# --------------------------------------------------
# FEATURE / LABEL SPLIT
# --------------------------------------------------

X = df[[
    "amount",
    "velocity",
    "night_txn",
    "is_card",
    "is_wallet"
]]

y = df["fraud"]

# --------------------------------------------------
# TRAIN MODEL
# --------------------------------------------------

model = LogisticRegression(max_iter=1000)
model.fit(X, y)

# --------------------------------------------------
# SAVE MODEL
# --------------------------------------------------

joblib.dump(model, "fraud_model.pkl")

print("✅ Fraud detection ML model trained and saved as fraud_model.pkl")
