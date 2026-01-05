// services/api.js
import axios from 'axios';

// Configure API Base URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/fraud-detection/api/v1';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Add request interceptor
api.interceptors.request.use(
  (config) => {
    console.log('📤 API Request:', config.method.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('❌ Request Error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('✅ API Response:', response.status, response.data);
    return response;
  },
  (error) => {
    console.error('❌ Response Error:', error.response?.status, error.message);
    return Promise.reject(error);
  }
);

// ✅ GET all transactions
export const getAllTransactions = async () => {
  try {
    const response = await api.get('/transactions');
    return response.data;
  } catch (error) {
    console.error('Error fetching transactions:', error);
    throw error;
  }
};

// ✅ GET single transaction by ID
export const getTransactionById = async (id) => {
  try {
    const response = await api.get(`/transactions/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching transaction:', error);
    throw error;
  }
};

// ✅ GET all fraudulent transactions
export const getFraudulentTransactions = async () => {
  try {
    const response = await api.get('/transactions/fraud/all');
    return response.data;
  } catch (error) {
    console.error('Error fetching fraudulent transactions:', error);
    throw error;
  }
};

// ✅ GET HIGH risk transactions
export const getHighRiskTransactions = async () => {
  try {
    const response = await api.get('/transactions/risk/high');
    return response.data;
  } catch (error) {
    console.error('Error fetching high risk transactions:', error);
    throw error;
  }
};

// ✅ GET MEDIUM risk transactions
export const getMediumRiskTransactions = async () => {
  try {
    const response = await api.get('/transactions/risk/medium');
    return response.data;
  } catch (error) {
    console.error('Error fetching medium risk transactions:', error);
    throw error;
  }
};

// ✅ GET transactions by account number
export const getTransactionsByAccount = async (accountNumber) => {
  try {
    const response = await api.get(`/transactions/account/${accountNumber}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching transactions by account:', error);
    throw error;
  }
};

// ✅ GET statistics
export const getTransactionStats = async () => {
  try {
    const response = await api.get('/transactions/stats');
    return response.data;
  } catch (error) {
    console.error('Error fetching stats:', error);
    throw error;
  }
};

// ✅ POST new transaction (single)
export const createTransaction = async (transactionData) => {
  try {
    const response = await api.post('/transactions', transactionData);
    return response.data;
  } catch (error) {
    console.error('Error creating transaction:', error);
    throw error;
  }
};

// ✅ POST batch transactions
export const createBatchTransactions = async (transactionsArray) => {
  try {
    const response = await api.post('/transactions/batch', transactionsArray);
    return response.data;
  } catch (error) {
    console.error('Error creating batch transactions:', error);
    throw error;
  }
};

// ✅ DELETE transaction
export const deleteTransaction = async (id) => {
  try {
    const response = await api.delete(`/transactions/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting transaction:', error);
    throw error;
  }
};

// ✅ HEALTH CHECK
export const healthCheck = async () => {
  try {
    const response = await api.get('/health');
    return response.data;
  } catch (error) {
    console.error('Health check failed:', error);
    throw error;
  }
};

// ✅ GET metrics summary
export const getMetricsSummary = async () => {
  try {
    const response = await api.get('/metrics/summary');
    return response.data;
  } catch (error) {
    console.error('Error fetching metrics summary:', error);
    throw error;
  }
};

// ✅ GET rule breakdown
export const getRuleBreakdown = async () => {
  try {
    const response = await api.get('/metrics/rule-breakdown');
    return response.data;
  } catch (error) {
    console.error('Error fetching rule breakdown:', error);
    throw error;
  }
};

// ✅ GET system effectiveness
export const getSystemEffectiveness = async () => {
  try {
    const response = await api.get('/metrics/effectiveness');
    return response.data;
  } catch (error) {
    console.error('Error fetching system effectiveness:', error);
    throw error;
  }
};

// ✅ GET risk distribution
export const getRiskDistribution = async () => {
  try {
    const response = await api.get('/metrics/risk-distribution');
    return response.data;
  } catch (error) {
    console.error('Error fetching risk distribution:', error);
    throw error;
  }
};

// ✅ GET time-based analysis
export const getTimeAnalysis = async (period = '7days') => {
  try {
    const response = await api.get(`/metrics/time-analysis?period=${period}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching time analysis:', error);
    throw error;
  }
};

// ✅ GET performance metrics
export const getPerformanceMetrics = async () => {
  try {
    const response = await api.get('/metrics/performance');
    return response.data;
  } catch (error) {
    console.error('Error fetching performance metrics:', error);
    throw error;
  }
};

// ✅ POST run fraud scenarios
export const runFraudScenarios = async () => {
  try {
    const response = await api.post('/scenarios/run-all');
    return response.data;
  } catch (error) {
    console.error('Error running fraud scenarios:', error);
    throw error;
  }
};

// ✅ POST test high value scenario
export const testHighValueScenario = async () => {
  try {
    const response = await api.post('/scenarios/high-value');
    return response.data;
  } catch (error) {
    console.error('Error testing high value scenario:', error);
    throw error;
  }
};

// ✅ POST test rapid transactions scenario
export const testRapidTransactionsScenario = async () => {
  try {
    const response = await api.post('/scenarios/rapid-transactions');
    return response.data;
  } catch (error) {
    console.error('Error testing rapid transactions scenario:', error);
    throw error;
  }
};

// ✅ POST test location mismatch scenario
export const testLocationMismatchScenario = async () => {
  try {
    const response = await api.post('/scenarios/location-mismatch');
    return response.data;
  } catch (error) {
    console.error('Error testing location mismatch scenario:', error);
    throw error;
  }
};

// ✅ POST test suspicious merchant scenario
export const testSuspiciousMerchantScenario = async () => {
  try {
    const response = await api.post('/scenarios/suspicious-merchant');
    return response.data;
  } catch (error) {
    console.error('Error testing suspicious merchant scenario:', error);
    throw error;
  }
};

// ✅ POST test odd hours scenario
export const testOddHoursScenario = async () => {
  try {
    const response = await api.post('/scenarios/odd-hours');
    return response.data;
  } catch (error) {
    console.error('Error testing odd hours scenario:', error);
    throw error;
  }
};

// ✅ GET scenario test results
export const getScenarioResults = async () => {
  try {
    const response = await api.get('/scenarios/results');
    return response.data;
  } catch (error) {
    console.error('Error fetching scenario results:', error);
    throw error;
  }
};

export default api;