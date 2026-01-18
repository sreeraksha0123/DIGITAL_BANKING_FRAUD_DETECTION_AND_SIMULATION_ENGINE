import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
import joblib

# Dummy training data (for college project – acceptable)
data = {
    "amount": [100, 5000, 200000, 50, 120000, 300, 90000, 40],
    "velocity": [1, 2, 15, 1, 12, 1, 10, 1],
    "night_txn": [0, 0, 1, 0, 1, 0, 1, 0],
    "fraud": [0, 0, 1, 0, 1, 0, 1, 0]
}

df = pd.DataFrame(data)

X = df[["amount", "velocity", "night_txn"]]
y = df["fraud"]

model = LogisticRegression()
model.fit(X, y)

joblib.dump(model, "fraud_model.pkl")

print("✅ ML model trained and saved as fraud_model.pkl")
