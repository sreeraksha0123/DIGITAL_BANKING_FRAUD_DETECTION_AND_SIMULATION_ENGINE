from flask import Flask, request, jsonify
import joblib
import datetime

app = Flask(__name__)

# Load trained model
model = joblib.load("fraud_model.pkl")

@app.route("/predict", methods=["POST"])
def predict():
    data = request.json

    amount = data.get("amount", 0)

    # simple feature engineering
    velocity = 10 if amount > 50000 else 1
    hour = datetime.datetime.now().hour
    night_txn = 1 if hour < 6 or hour > 22 else 0

    X = [[amount, velocity, night_txn]]

    prediction = model.predict(X)[0]
    probability = model.predict_proba(X)[0][1]

    return jsonify({
        "prediction": "FRAUD" if prediction == 1 else "SAFE",
        "probability": round(float(probability), 2)
    })

if __name__ == "__main__":
    app.run(port=5000)
