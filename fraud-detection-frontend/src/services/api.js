// services/api.js
import axios from "axios";

/**
 * =====================================================
 * API CONFIGURATION
 * =====================================================
 */

// Base URL (env first, fallback for local dev)
const API_BASE_URL =
  process.env.REACT_APP_API_URL ||
  "http://localhost:8081/fraud-detection/api/v1";

// Axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

/**
 * =====================================================
 * INTERCEPTORS (LOGGING ONLY)
 * =====================================================
 */

// Request interceptor
api.interceptors.request.use(
  (config) => {
    console.log(
      "📤 API Request:",
      config.method?.toUpperCase(),
      config.url
    );
    return config;
  },
  (error) => {
    console.error("❌ Request Error:", error);
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log("✅ API Response:", response.status, response.data);
    return response;
  },
  (error) => {
    console.error(
      "❌ API Response Error:",
      error.response?.status,
      error.message
    );
    return Promise.reject(error);
  }
);

/**
 * =====================================================
 * TRANSACTIONS (READ + CREATE)
 * =====================================================
 */

/**
 * Get all transactions
 * Backend already includes fraud, risk, approval info
 */
export const getAllTransactions = async () => {
  const response = await api.get("/transactions");
  return response.data;
};

// ✅ GET dashboard metrics (SOURCE OF TRUTH)
export const getDashboardMetrics = async () => {
  try {
    const response = await api.get('/metrics/summary');
    return response.data;
  } catch (error) {
    console.error('Error fetching dashboard metrics:', error);
    throw error;
  }
};

/**
 * Get single transaction by ID
 */
export const getTransactionById = async (id) => {
  const response = await api.get(`/transactions/${id}`);
  return response.data;
};

/**
 * Create a new transaction
 * Fraud detection happens server-side
 */
export const createTransaction = async (transactionData) => {
  const response = await api.post("/transactions", transactionData);
  return response.data;
};

/**
 * Delete a transaction (optional – admin/testing only)
 */
export const deleteTransaction = async (id) => {
  const response = await api.delete(`/transactions/${id}`);
  return response.data;
};

/**
 * =====================================================
 * DASHBOARD METRICS (SINGLE SOURCE OF TRUTH)
 * =====================================================


/**
 * =====================================================
 * HEALTH CHECK (OPTIONAL)
 * =====================================================
 */

export const healthCheck = async () => {
  const response = await api.get("/health");
  return response.data;
};

export default api;
