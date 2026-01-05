import React, { useState, useEffect, useRef } from 'react';
import {
  Search, Filter, Eye, EyeOff, AlertCircle, CheckCircle, Clock,
  RefreshCw, Bell, Download, Activity, Shield, TrendingUp, AlertTriangle,
  BarChart3, CreditCard, MapPin, User, Server, Database, PlayCircle, XCircle, Clock as ClockIcon
} from 'lucide-react';
import { getAllTransactions, getMetricsSummary, getRuleBreakdown, getSystemEffectiveness, runFraudScenarios } from '../services/api';

export default function Dashboard() {
  const [transactions, setTransactions] = useState([]);
  const [filteredTransactions, setFilteredTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('latest');
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [showAlerts, setShowAlerts] = useState(true);
  const [activeTab, setActiveTab] = useState('transactions'); // New state for tabs
  const autoRefreshIntervalRef = useRef(null);

  // New states for metrics
  const [metrics, setMetrics] = useState(null);
  const [ruleBreakdown, setRuleBreakdown] = useState(null);
  const [effectiveness, setEffectiveness] = useState(null);
  const [scenarioResults, setScenarioResults] = useState(null);
  const [scenarioLoading, setScenarioLoading] = useState(false);

  const [filters, setFilters] = useState({
    riskLevel: 'ALL',
    approvalStatus: 'ALL',
    fraudStatus: 'ALL',
  });
  const [expandedRow, setExpandedRow] = useState(null);

  // Fetch transactions
  const fetchTransactions = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await getAllTransactions();
      const transactionList = Array.isArray(data) ? data : (data ? [data] : []);

      const newHighRisk = transactionList.filter(t => t.riskLevel === 'HIGH');
      if (newHighRisk.length > 0 && showAlerts) {
        playAlertSound();
      }

      setTransactions(transactionList);
      setFilteredTransactions(transactionList);
    } catch (error) {
      console.error('Failed to fetch transactions:', error);
      setError('Failed to load transactions. Please check backend connection.');
    } finally {
      setLoading(false);
    }
  };

  // Fetch metrics
  const fetchMetrics = async () => {
    try {
      const [summary, breakdown, eff] = await Promise.all([
        getMetricsSummary(),
        getRuleBreakdown(),
        getSystemEffectiveness()
      ]);
      setMetrics(summary);
      setRuleBreakdown(breakdown);
      setEffectiveness(eff);
    } catch (error) {
      console.error('Error fetching metrics:', error);
    }
  };

  // Run fraud scenarios
  const runAllScenarios = async () => {
    try {
      setScenarioLoading(true);
      const results = await runFraudScenarios();
      setScenarioResults(results);
      // Refresh transactions to show test data
      await fetchTransactions();
    } catch (error) {
      console.error('Error running scenarios:', error);
    } finally {
      setScenarioLoading(false);
    }
  };

  // Play alert sound
  const playAlertSound = () => {
    try {
      const audioContext = new (window.AudioContext || window.webkitAudioContext)();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);

      oscillator.frequency.value = 800;
      oscillator.type = 'sine';

      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);

      oscillator.start(audioContext.currentTime);
      oscillator.stop(audioContext.currentTime + 0.5);
    } catch (err) {
      console.log('Audio alert not available');
    }
  };

  // Initial fetch
  useEffect(() => {
    fetchTransactions();
    fetchMetrics();
  }, []);

  // Auto-refresh
  useEffect(() => {
    if (autoRefresh && activeTab === 'transactions') {
      autoRefreshIntervalRef.current = setInterval(() => {
        fetchTransactions();
      }, 5000);
    } else {
      if (autoRefreshIntervalRef.current) {
        clearInterval(autoRefreshIntervalRef.current);
      }
    }

    return () => {
      if (autoRefreshIntervalRef.current) {
        clearInterval(autoRefreshIntervalRef.current);
      }
    };
  }, [autoRefresh, showAlerts, activeTab]);

  // Apply filters & sorting
  useEffect(() => {
    let result = transactions;

    if (filters.riskLevel !== 'ALL') {
      result = result.filter(t => t.riskLevel === filters.riskLevel);
    }

    if (filters.approvalStatus !== 'ALL') {
      result = result.filter(t => t.approvalStatus === filters.approvalStatus);
    }

    if (filters.fraudStatus !== 'ALL') {
      result = result.filter(t => t.isFraud === (filters.fraudStatus === 'FRAUD'));
    }

    if (searchTerm) {
      result = result.filter(t =>
        t.accountNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        t.location?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        t.transactionType?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    if (sortBy === 'latest') {
      result = result.sort((a, b) => b.id - a.id);
    } else if (sortBy === 'highest-score') {
      result = result.sort((a, b) => (b.fraudScore || 0) - (a.fraudScore || 0));
    } else if (sortBy === 'highest-risk') {
      const riskOrder = { 'HIGH': 0, 'MEDIUM': 1, 'LOW': 2 };
      result = result.sort((a, b) =>
        (riskOrder[a.riskLevel] || 3) - (riskOrder[b.riskLevel] || 3)
      );
    }

    setFilteredTransactions(result);
  }, [filters, searchTerm, transactions, sortBy]);

  // Analytics
  const analytics = {
    fraudRate: transactions.length > 0
      ? ((transactions.filter(t => t.isFraud).length / transactions.length) * 100).toFixed(1)
      : 0,
    avgFraudScore: transactions.length > 0
      ? (transactions.reduce((sum, t) => sum + (t.fraudScore || 0), 0) / transactions.length).toFixed(0)
      : 0,
    blockedAmount: transactions
      .filter(t => t.approvalStatus === 'FAILURE')
      .reduce((sum, t) => sum + (t.amount || 0), 0),
    highRiskCount: transactions.filter(t => t.riskLevel === 'HIGH').length,
  };

  // Export CSV
  const exportToCSV = () => {
    const headers = ['ID', 'Account', 'Amount', 'Location', 'Risk Level', 'Score', 'Status', 'Fraud'];
    const rows = filteredTransactions.map(t => [
      t.id,
      t.accountNumber,
      t.amount,
      t.location,
      t.riskLevel,
      t.fraudScore,
      t.approvalStatus,
      t.isFraud ? 'Yes' : 'No'
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `fraud-report-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
  };

  const getRiskLevelColor = (level) => {
    const colors = {
      'LOW': { bg: '#ecfdf5', text: '#047857', border: '#d1fae5' },
      'MEDIUM': { bg: '#fffbeb', text: '#b45309', border: '#fef3c7' },
      'HIGH': { bg: '#fef2f2', text: '#dc2626', border: '#fecaca' },
    };
    return colors[level] || { bg: '#f3f4f6', text: '#6b7280', border: '#e5e7eb' };
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'SUCCESS':
        return <CheckCircle size={16} color="#059669" />;
      case 'PENDING':
        return <Clock size={16} color="#d97706" />;
      case 'FAILURE':
        return <AlertCircle size={16} color="#dc2626" />;
      default:
        return null;
    }
  };

  const stats = {
    total: transactions.length,
    fraud: transactions.filter(t => t.isFraud).length,
    pending: transactions.filter(t => t.approvalStatus === 'PENDING').length,
    highRisk: transactions.filter(t => t.riskLevel === 'HIGH').length
  };

  const styles = {
    container: {
      minHeight: '100vh',
      backgroundColor: '#f8fafc',
      fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Inter", sans-serif'
    },
    header: {
      backgroundColor: '#ffffff',
      borderBottom: '1px solid #e2e8f0',
      padding: '1.5rem 0',
      position: 'sticky',
      top: 0,
      zIndex: 10,
      boxShadow: '0 1px 3px 0 rgb(0 0 0 / 0.1)',
    },
    headerContent: {
      maxWidth: '1280px',
      margin: '0 auto',
      padding: '0 1.5rem',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
    },
    titleSection: {
      display: 'flex',
      alignItems: 'center',
      gap: '1rem',
    },
    title: {
      fontSize: '1.5rem',
      fontWeight: 600,
      color: '#1e293b',
      margin: 0,
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
    },
    subtitle: {
      color: '#64748b',
      margin: '0.25rem 0 0 0',
      fontSize: '0.875rem',
    },
    headerRight: {
      display: 'flex',
      gap: '1rem',
      alignItems: 'center',
    },
    statusBadge: {
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      padding: '0.5rem 1rem',
      backgroundColor: '#f1f5f9',
      color: '#475569',
      borderRadius: '0.375rem',
      fontSize: '0.875rem',
      fontWeight: 500,
    },
    mainContent: {
      maxWidth: '1280px',
      margin: '0 auto',
      padding: '2rem 1.5rem',
    },
    // Tab Navigation
    tabNavigation: {
      display: 'flex',
      gap: '0.5rem',
      marginBottom: '2rem',
      borderBottom: '1px solid #e2e8f0',
      paddingBottom: '0.5rem'
    },
    tabButton: (active) => ({
      padding: '0.75rem 1.5rem',
      border: 'none',
      background: active ? '#3b82f6' : 'transparent',
      color: active ? '#ffffff' : '#64748b',
      borderRadius: '0.5rem 0.5rem 0 0',
      cursor: 'pointer',
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      fontWeight: 500,
      fontSize: '0.875rem',
      transition: 'all 0.2s',
    }),
    // Metrics Grid
    metricsGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
      gap: '1rem',
      marginBottom: '2rem',
    },
    metricCard: {
      backgroundColor: '#ffffff',
      borderRadius: '0.5rem',
      border: '1px solid #e2e8f0',
      padding: '1.25rem',
      boxShadow: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    },
    metricHeader: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'flex-start',
      marginBottom: '0.75rem',
    },
    metricLabel: {
      fontSize: '0.875rem',
      fontWeight: 500,
      color: '#64748b',
    },
    metricValue: {
      fontSize: '1.5rem',
      fontWeight: 600,
      marginTop: '0.25rem',
      color: '#1e293b',
    },
    metricSubtext: {
      fontSize: '0.75rem',
      color: '#94a3b8',
      marginTop: '0.25rem',
    },
    // Advanced Metrics
    advancedMetricsGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
      gap: '1rem',
      marginBottom: '2rem',
    },
    advancedMetricCard: {
      backgroundColor: '#ffffff',
      borderRadius: '0.5rem',
      border: '1px solid #e2e8f0',
      padding: '1.5rem',
      boxShadow: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    },
    // Controls Section
    controlsSection: {
      backgroundColor: '#ffffff',
      borderRadius: '0.5rem',
      border: '1px solid #e2e8f0',
      padding: '1.5rem',
      marginBottom: '1.5rem',
      boxShadow: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    },
    sectionHeader: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: '1.5rem',
    },
    sectionTitle: {
      fontSize: '1rem',
      fontWeight: 600,
      color: '#1e293b',
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
    },
    buttonGroup: {
      display: 'flex',
      gap: '0.5rem',
    },
    btn: {
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      padding: '0.5rem 0.75rem',
      fontSize: '0.875rem',
      border: '1px solid #e2e8f0',
      borderRadius: '0.375rem',
      backgroundColor: '#ffffff',
      color: '#475569',
      cursor: 'pointer',
      transition: 'all 0.15s ease',
    },
    btnPrimary: {
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      padding: '0.5rem 0.75rem',
      fontSize: '0.875rem',
      border: '1px solid #3b82f6',
      borderRadius: '0.375rem',
      backgroundColor: '#3b82f6',
      color: '#ffffff',
      cursor: 'pointer',
      transition: 'all 0.15s ease',
    },
    toggleBtn: (active) => ({
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
      padding: '0.5rem 0.75rem',
      fontSize: '0.875rem',
      border: '1px solid',
      borderColor: active ? '#3b82f6' : '#e2e8f0',
      borderRadius: '0.375rem',
      backgroundColor: active ? '#eff6ff' : '#ffffff',
      color: active ? '#1d4ed8' : '#475569',
      cursor: 'pointer',
      transition: 'all 0.15s ease',
    }),
    searchBox: {
      position: 'relative',
      marginBottom: '1.5rem',
    },
    searchInput: {
      width: '100%',
      padding: '0.625rem 1rem 0.625rem 2.5rem',
      border: '1px solid #cbd5e1',
      borderRadius: '0.375rem',
      fontSize: '0.875rem',
      boxSizing: 'border-box',
      backgroundColor: '#ffffff',
    },
    filtersRow: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
      gap: '1rem',
    },
    filterGroup: {
      display: 'flex',
      flexDirection: 'column',
    },
    filterLabel: {
      fontSize: '0.75rem',
      fontWeight: 500,
      color: '#475569',
      marginBottom: '0.375rem',
      textTransform: 'uppercase',
      letterSpacing: '0.05em',
    },
    filterSelect: {
      padding: '0.5rem',
      border: '1px solid #cbd5e1',
      borderRadius: '0.375rem',
      fontSize: '0.875rem',
      backgroundColor: '#ffffff',
      cursor: 'pointer',
    },
    tableContainer: {
      backgroundColor: '#ffffff',
      borderRadius: '0.5rem',
      border: '1px solid #e2e8f0',
      overflow: 'hidden',
      boxShadow: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    },
    table: {
      width: '100%',
      borderCollapse: 'collapse',
    },
    tableHeader: {
      backgroundColor: '#f8fafc',
      borderBottom: '1px solid #e2e8f0',
    },
    th: {
      padding: '1rem',
      textAlign: 'left',
      fontSize: '0.75rem',
      fontWeight: 600,
      color: '#475569',
      textTransform: 'uppercase',
      letterSpacing: '0.05em',
    },
    td: {
      padding: '1rem',
      fontSize: '0.875rem',
      color: '#334155',
      borderBottom: '1px solid #e2e8f0',
    },
    badge: {
      display: 'inline-block',
      padding: '0.25rem 0.75rem',
      borderRadius: '9999px',
      fontSize: '0.75rem',
      fontWeight: 500,
      textAlign: 'center',
    },
    scoreBadge: (score) => ({
      display: 'inline-block',
      padding: '0.25rem 0.75rem',
      borderRadius: '9999px',
      fontSize: '0.75rem',
      fontWeight: 600,
      backgroundColor: score > 60 ? '#fef2f2' : score > 30 ? '#fffbeb' : '#f0f9ff',
      color: score > 60 ? '#dc2626' : score > 30 ? '#b45309' : '#0369a1',
    }),
    noData: {
      textAlign: 'center',
      padding: '3rem',
      color: '#94a3b8',
      fontSize: '0.875rem',
    },
    errorBox: {
      backgroundColor: '#fef2f2',
      border: '1px solid #fecaca',
      borderRadius: '0.5rem',
      padding: '1rem',
      marginBottom: '1rem',
      color: '#991b1b',
      fontSize: '0.875rem',
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
    },
    loadingRow: {
      padding: '2rem',
      textAlign: 'center',
      color: '#64748b',
      fontSize: '0.875rem',
    },
    expandableContent: {
      backgroundColor: '#f8fafc',
      padding: '1.5rem',
    },
    detailGrid: {
      display: 'grid',
      gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
      gap: '1.5rem',
    },
    detailSection: {
      display: 'flex',
      flexDirection: 'column',
      gap: '0.75rem',
    },
    detailTitle: {
      fontSize: '0.875rem',
      fontWeight: 600,
      color: '#475569',
      display: 'flex',
      alignItems: 'center',
      gap: '0.5rem',
    },
    detailItem: {
      display: 'flex',
      justifyContent: 'space-between',
      padding: '0.5rem 0',
      borderBottom: '1px solid #e2e8f0',
    },
    detailLabel: {
      fontSize: '0.875rem',
      color: '#64748b',
    },
    detailValue: {
      fontSize: '0.875rem',
      color: '#1e293b',
      fontWeight: 500,
    },
    footer: {
      marginTop: '2rem',
      paddingTop: '1rem',
      borderTop: '1px solid #e2e8f0',
      color: '#64748b',
      fontSize: '0.75rem',
    },
    // Testing Panel Styles
    testingPanel: {
      backgroundColor: '#ffffff',
      borderRadius: '0.5rem',
      border: '1px solid #e2e8f0',
      padding: '1.5rem',
      marginBottom: '1.5rem',
      boxShadow: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    },
    scenarioCard: {
      backgroundColor: '#f8fafc',
      borderRadius: '0.5rem',
      border: '1px solid #e2e8f0',
      padding: '1rem',
      marginBottom: '1rem',
    },
    scenarioResult: {
      display: 'flex',
      alignItems: 'center',
      gap: '1rem',
      padding: '0.75rem',
      backgroundColor: '#f8fafc',
      borderRadius: '0.375rem',
      marginBottom: '0.5rem',
    },
  };

  // Render Metrics Tab
  const renderMetricsTab = () => (
    <div>
      {/* Advanced Metrics */}
      <div style={styles.advancedMetricsGrid}>
        <div style={styles.advancedMetricCard}>
          <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>
            <BarChart3 size={18} style={{ marginRight: '0.5rem' }} />
            System Effectiveness
          </h3>
          {effectiveness ? (
            <div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '1rem' }}>
                <div>
                  <div style={styles.metricLabel}>Detection Rate</div>
                  <div style={{ ...styles.metricValue, color: effectiveness.detectionRate >= 90 ? '#059669' : '#d97706' }}>
                    {effectiveness.detectionRate}%
                  </div>
                </div>
                <div>
                  <div style={styles.metricLabel}>False Positive Rate</div>
                  <div style={{ ...styles.metricValue, color: effectiveness.falsePositiveRate <= 5 ? '#059669' : '#dc2626' }}>
                    {effectiveness.falsePositiveRate}%
                  </div>
                </div>
              </div>
              <div style={{ marginTop: '1rem' }}>
                <div style={styles.metricLabel}>System Rating</div>
                <div style={{
                  padding: '0.5rem 1rem',
                  backgroundColor: effectiveness.rating === 'EXCELLENT' ? '#d1fae5' :
                                 effectiveness.rating === 'GOOD' ? '#fef3c7' :
                                 effectiveness.rating === 'FAIR' ? '#fde68a' : '#fee2e2',
                  color: effectiveness.rating === 'EXCELLENT' ? '#065f46' :
                         effectiveness.rating === 'GOOD' ? '#92400e' :
                         effectiveness.rating === 'FAIR' ? '#92400e' : '#991b1b',
                  borderRadius: '0.375rem',
                  fontWeight: 600,
                  display: 'inline-block',
                  marginTop: '0.5rem'
                }}>
                  {effectiveness.rating}
                </div>
              </div>
            </div>
          ) : (
            <div style={styles.noData}>Loading effectiveness metrics...</div>
          )}
        </div>

        <div style={styles.advancedMetricCard}>
          <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>
            <TrendingUp size={18} style={{ marginRight: '0.5rem' }} />
            Rule Breakdown
          </h3>
          {ruleBreakdown && ruleBreakdown.rulePercentages ? (
            <div>
              {Object.entries(ruleBreakdown.rulePercentages).map(([rule, percentage]) => (
                <div key={rule} style={{ marginBottom: '0.75rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.25rem' }}>
                    <span style={{ fontSize: '0.875rem', color: '#475569' }}>{rule}</span>
                    <span style={{ fontSize: '0.875rem', fontWeight: 600, color: '#1e293b' }}>{percentage}%</span>
                  </div>
                  <div style={{
                    width: '100%',
                    height: '6px',
                    backgroundColor: '#e2e8f0',
                    borderRadius: '3px',
                    overflow: 'hidden'
                  }}>
                    <div style={{
                      width: `${percentage}%`,
                      height: '100%',
                      backgroundColor: '#3b82f6',
                      borderRadius: '3px'
                    }}></div>
                  </div>
                </div>
              ))}
              <div style={{ marginTop: '1rem', fontSize: '0.75rem', color: '#64748b' }}>
                Most common: {ruleBreakdown.mostCommonRule}
              </div>
            </div>
          ) : (
            <div style={styles.noData}>Loading rule breakdown...</div>
          )}
        </div>

        <div style={styles.advancedMetricCard}>
          <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>
            <Activity size={18} style={{ marginRight: '0.5rem' }} />
            Performance Metrics
          </h3>
          {metrics ? (
            <div>
              <div style={styles.detailItem}>
                <span style={styles.detailLabel}>Total Transactions:</span>
                <span style={styles.detailValue}>{metrics.totalTransactions}</span>
              </div>
              <div style={styles.detailItem}>
                <span style={styles.detailLabel}>Blocked Amount:</span>
                <span style={styles.detailValue}>{metrics.blockedAmountFormatted}</span>
              </div>
              <div style={styles.detailItem}>
                <span style={styles.detailLabel}>Data Since:</span>
                <span style={styles.detailValue}>
                  {new Date(metrics.dataSince).toLocaleDateString()}
                </span>
              </div>
              <div style={styles.detailItem}>
                <span style={styles.detailLabel}>Last Updated:</span>
                <span style={styles.detailValue}>
                  {new Date(metrics.lastUpdated).toLocaleTimeString()}
                </span>
              </div>
            </div>
          ) : (
            <div style={styles.noData}>Loading performance metrics...</div>
          )}
        </div>
      </div>

      {/* Recommendations */}
      {effectiveness && effectiveness.recommendations && (
        <div style={styles.advancedMetricCard}>
          <h3 style={{ fontSize: '1rem', fontWeight: 600, color: '#1e293b', marginBottom: '1rem' }}>
            <AlertTriangle size={18} style={{ marginRight: '0.5rem', color: '#f59e0b' }} />
            Recommendations
          </h3>
          <ul style={{ margin: 0, paddingLeft: '1.5rem' }}>
            {effectiveness.recommendations.map((rec, index) => (
              <li key={index} style={{ marginBottom: '0.5rem', color: '#475569', fontSize: '0.875rem' }}>
                {rec}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );

  // Render Fraud Testing Tab
  const renderTestingTab = () => (
    <div>
      <div style={styles.testingPanel}>
        <div style={styles.sectionHeader}>
          <h2 style={styles.sectionTitle}>
            <PlayCircle size={18} />
            Fraud Scenario Testing
          </h2>
          <button
            onClick={runAllScenarios}
            disabled={scenarioLoading}
            style={{
              ...styles.btnPrimary,
              backgroundColor: scenarioLoading ? '#94a3b8' : '#10b981'
            }}
          >
            {scenarioLoading ? (
              <>
                <ClockIcon size={16} className="animate-spin" />
                Running Tests...
              </>
            ) : (
              <>
                <PlayCircle size={16} />
                Run All Scenarios
              </>
            )}
          </button>
        </div>

        <p style={{ color: '#64748b', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
          Test the fraud detection system with predefined scenarios to verify rule effectiveness.
        </p>

        {scenarioResults && (
          <div style={styles.scenarioCard}>
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '1rem',
              marginBottom: '1rem',
              padding: '1rem',
              backgroundColor: scenarioResults.overallStatus === 'ALL_PASSED' ? '#f0fdf4' : '#fef2f2',
              border: `1px solid ${scenarioResults.overallStatus === 'ALL_PASSED' ? '#bbf7d0' : '#fecaca'}`,
              borderRadius: '0.375rem',
            }}>
              <div style={{
                width: '40px',
                height: '40px',
                borderRadius: '50%',
                backgroundColor: scenarioResults.overallStatus === 'ALL_PASSED' ? '#10b981' : '#dc2626',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#ffffff',
                fontWeight: 'bold',
              }}>
                {scenarioResults.successRate?.toFixed(0)}%
              </div>
              <div>
                <div style={{ fontWeight: 600, color: '#1e293b' }}>
                  {scenarioResults.passed} of {scenarioResults.totalScenarios} scenarios passed
                </div>
                <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                  Success rate: {scenarioResults.successRate?.toFixed(1)}%
                </div>
              </div>
            </div>

            <div>
              <h4 style={{ fontSize: '0.875rem', fontWeight: 600, color: '#475569', marginBottom: '0.75rem' }}>
                Individual Results:
              </h4>
              {scenarioResults.scenarios?.map((scenario, index) => (
                <div key={index} style={styles.scenarioResult}>
                  {scenario.testPassed ? (
                    <CheckCircle size={20} color="#10b981" />
                  ) : (
                    <XCircle size={20} color="#dc2626" />
                  )}
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 500, color: '#1e293b' }}>{scenario.scenario}</div>
                    <div style={{ fontSize: '0.75rem', color: '#64748b' }}>
                      Score: {scenario.score || 0}, Risk: {scenario.risk || 'N/A'}
                    </div>
                  </div>
                  <div style={{
                    fontSize: '0.75rem',
                    fontWeight: 500,
                    color: scenario.testPassed ? '#059669' : '#dc2626',
                    padding: '0.25rem 0.5rem',
                    backgroundColor: scenario.testPassed ? '#d1fae5' : '#fee2e2',
                    borderRadius: '9999px',
                  }}>
                    {scenario.testPassed ? 'PASSED' : 'FAILED'}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        <div style={{ marginTop: '1.5rem' }}>
          <h4 style={{ fontSize: '0.875rem', fontWeight: 600, color: '#475569', marginBottom: '0.75rem' }}>
            Available Test Scenarios:
          </h4>
          <div style={{ display: 'grid', gap: '0.75rem' }}>
            {[
              { name: 'High Value Transaction', desc: 'Transaction > $100,000' },
              { name: 'Rapid Transactions', desc: '>10 transactions per hour' },
              { name: 'Location Mismatch', desc: 'Unusual geographic location' },
              { name: 'Suspicious Merchant', desc: 'High-risk merchant' },
              { name: 'Odd Hours', desc: 'Transaction at 3:00 AM' },
            ].map((scenario, index) => (
              <div key={index} style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '0.75rem',
                backgroundColor: '#f8fafc',
                borderRadius: '0.375rem',
                border: '1px solid #e2e8f0',
              }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <AlertTriangle size={16} color="#f59e0b" />
                    <span style={{ fontWeight: 500, color: '#1e293b' }}>{scenario.name}</span>
                  </div>
                  <div style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '0.25rem' }}>
                    {scenario.desc}
                  </div>
                </div>
                <div style={{
                  fontSize: '0.75rem',
                  fontWeight: 500,
                  color: '#3b82f6',
                  padding: '0.25rem 0.5rem',
                  backgroundColor: '#eff6ff',
                  borderRadius: '9999px',
                }}>
                  TEST SCENARIO
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <div style={styles.headerContent}>
          <div style={styles.titleSection}>
            <Shield size={24} color="#3b82f6" />
            <div>
              <h1 style={styles.title}>Fraud Detection Dashboard</h1>
              <p style={styles.subtitle}>Real-time transaction monitoring & analysis</p>
            </div>
          </div>
          <div style={styles.headerRight}>
            <div style={styles.statusBadge}>
              <Activity size={16} />
              {autoRefresh ? 'Live Monitoring' : 'Manual Refresh'}
            </div>
          </div>
        </div>
      </div>

      <div style={styles.mainContent}>
        {/* Tab Navigation */}
        <div style={styles.tabNavigation}>
          <button
            onClick={() => setActiveTab('transactions')}
            style={styles.tabButton(activeTab === 'transactions')}
          >
            <CreditCard size={16} />
            Transactions
          </button>
          <button
            onClick={() => setActiveTab('metrics')}
            style={styles.tabButton(activeTab === 'metrics')}
          >
            <BarChart3 size={16} />
            Analytics & Metrics
          </button>
          <button
            onClick={() => setActiveTab('testing')}
            style={styles.tabButton(activeTab === 'testing')}
          >
            <PlayCircle size={16} />
            Fraud Testing
          </button>
        </div>

        {/* Key Metrics (Always visible) */}
        <div style={styles.metricsGrid}>
          <div style={styles.metricCard}>
            <div style={styles.metricHeader}>
              <div style={styles.metricLabel}>Fraud Rate</div>
              <AlertTriangle size={16} color={analytics.fraudRate > 20 ? '#dc2626' : '#64748b'} />
            </div>
            <div style={{ ...styles.metricValue, color: analytics.fraudRate > 20 ? '#dc2626' : '#059669' }}>
              {analytics.fraudRate}%
            </div>
            <div style={styles.metricSubtext}>
              {analytics.fraudRate > 20 ? 'Above threshold' : 'Within normal range'}
            </div>
          </div>

          <div style={styles.metricCard}>
            <div style={styles.metricHeader}>
              <div style={styles.metricLabel}>Avg. Fraud Score</div>
              <BarChart3 size={16} color="#64748b" />
            </div>
            <div style={styles.metricValue}>{analytics.avgFraudScore}</div>
            <div style={styles.metricSubtext}>Scale: 0-100</div>
          </div>

          <div style={styles.metricCard}>
            <div style={styles.metricHeader}>
              <div style={styles.metricLabel}>High Risk Transactions</div>
              <AlertCircle size={16} color="#dc2626" />
            </div>
            <div style={{ ...styles.metricValue, color: '#dc2626' }}>
              {analytics.highRiskCount}
            </div>
            <div style={styles.metricSubtext}>
              {transactions.length > 0 ? `${((analytics.highRiskCount / transactions.length) * 100).toFixed(1)}% of total` : 'No data'}
            </div>
          </div>

          <div style={styles.metricCard}>
            <div style={styles.metricHeader}>
              <div style={styles.metricLabel}>Blocked Amount</div>
              <CreditCard size={16} color="#64748b" />
            </div>
            <div style={styles.metricValue}>
              ${(analytics.blockedAmount / 1000).toFixed(1)}K
            </div>
            <div style={styles.metricSubtext}>Prevented fraud losses</div>
          </div>
        </div>

        {/* Error Message */}
        {error && (
          <div style={styles.errorBox}>
            <AlertCircle size={16} />
            <span>{error}</span>
          </div>
        )}

        {/* Transactions Tab Content */}
        {activeTab === 'transactions' && (
          <>
            {/* Controls & Filters */}
            <div style={styles.controlsSection}>
              <div style={styles.sectionHeader}>
                <h2 style={styles.sectionTitle}>
                  <Filter size={18} />
                  Filters & Controls
                </h2>
                <div style={styles.buttonGroup}>
                  <button
                    style={styles.toggleBtn(autoRefresh)}
                    onClick={() => setAutoRefresh(!autoRefresh)}
                    title={autoRefresh ? 'Pause auto-refresh' : 'Enable auto-refresh'}
                  >
                    <RefreshCw size={16} />
                    {autoRefresh ? 'Auto Refresh' : 'Manual'}
                  </button>
                  <button
                    style={styles.toggleBtn(showAlerts)}
                    onClick={() => setShowAlerts(!showAlerts)}
                    title={showAlerts ? 'Disable alerts' : 'Enable alerts'}
                  >
                    <Bell size={16} />
                    Alerts
                  </button>
                  <button
                    style={styles.btn}
                    onClick={exportToCSV}
                    title="Export to CSV"
                  >
                    <Download size={16} />
                    Export
                  </button>
                  <button
                    style={styles.btn}
                    onClick={fetchTransactions}
                    title="Manual refresh"
                  >
                    <RefreshCw size={16} />
                    Refresh
                  </button>
                </div>
              </div>

              {/* Search */}
              <div style={styles.searchBox}>
                <Search size={16} style={{ position: 'absolute', left: '0.75rem', top: '0.75rem', color: '#94a3b8' }} />
                <input
                  type="text"
                  placeholder="Search transactions..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  style={styles.searchInput}
                />
              </div>

              {/* Filter Controls */}
              <div style={styles.filtersRow}>
                <div style={styles.filterGroup}>
                  <label style={styles.filterLabel}>Risk Level</label>
                  <select
                    value={filters.riskLevel}
                    onChange={(e) => setFilters({ ...filters, riskLevel: e.target.value })}
                    style={styles.filterSelect}
                  >
                    <option value="ALL">All Risk Levels</option>
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                  </select>
                </div>

                <div style={styles.filterGroup}>
                  <label style={styles.filterLabel}>Status</label>
                  <select
                    value={filters.approvalStatus}
                    onChange={(e) => setFilters({ ...filters, approvalStatus: e.target.value })}
                    style={styles.filterSelect}
                  >
                    <option value="ALL">All Statuses</option>
                    <option value="SUCCESS">Approved</option>
                    <option value="PENDING">Pending</option>
                    <option value="FAILURE">Blocked</option>
                  </select>
                </div>

                <div style={styles.filterGroup}>
                  <label style={styles.filterLabel}>Fraud Status</label>
                  <select
                    value={filters.fraudStatus}
                    onChange={(e) => setFilters({ ...filters, fraudStatus: e.target.value })}
                    style={styles.filterSelect}
                  >
                    <option value="ALL">All Transactions</option>
                    <option value="FRAUD">Fraudulent</option>
                    <option value="LEGITIMATE">Legitimate</option>
                  </select>
                </div>

                <div style={styles.filterGroup}>
                  <label style={styles.filterLabel}>Sort By</label>
                  <select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    style={styles.filterSelect}
                  >
                    <option value="latest">Most Recent</option>
                    <option value="highest-score">Highest Score</option>
                    <option value="highest-risk">Highest Risk</option>
                  </select>
                </div>

                <div style={styles.filterGroup}>
                  <label style={styles.filterLabel}>Actions</label>
                  <button
                    onClick={() => {
                      setFilters({ riskLevel: 'ALL', approvalStatus: 'ALL', fraudStatus: 'ALL' });
                      setSearchTerm('');
                      setSortBy('latest');
                    }}
                    style={{
                      ...styles.filterSelect,
                      backgroundColor: '#f8fafc',
                      cursor: 'pointer',
                      textAlign: 'center',
                    }}
                  >
                    Reset Filters
                  </button>
                </div>
              </div>
            </div>

            {/* Transactions Table */}
            <div style={styles.tableContainer}>
              <table style={styles.table}>
                <thead style={styles.tableHeader}>
                  <tr>
                    <th style={styles.th}>Account</th>
                    <th style={styles.th}>Type</th>
                    <th style={styles.th}>Amount</th>
                    <th style={styles.th}>Location</th>
                    <th style={styles.th}>Risk Score</th>
                    <th style={styles.th}>Risk Level</th>
                    <th style={styles.th}>Status</th>
                    <th style={styles.th}></th>
                  </tr>
                </thead>
                <tbody>
                  {loading ? (
                    <tr>
                      <td colSpan="8" style={styles.loadingRow}>
                        <RefreshCw size={20} className="animate-spin" style={{ margin: '0 auto 0.5rem', display: 'block' }} />
                        Loading transactions...
                      </td>
                    </tr>
                  ) : filteredTransactions.length === 0 ? (
                    <tr>
                      <td colSpan="8" style={styles.noData}>
                        <Database size={24} style={{ marginBottom: '0.5rem', opacity: 0.5 }} />
                        <div>No transactions found</div>
                        {transactions.length > 0 && <div style={{ fontSize: '0.75rem', marginTop: '0.25rem' }}>Try adjusting your filters</div>}
                      </td>
                    </tr>
                  ) : (
                    filteredTransactions.map((tx) => (
                      <React.Fragment key={tx.id}>
                        <tr
                          style={{
                            backgroundColor: tx.riskLevel === 'HIGH' ? '#fef2f210' : '#ffffff',
                            transition: 'background-color 0.15s ease',
                          }}
                        >
                          <td style={styles.td}>
                            <div style={{ fontWeight: 500 }}>{tx.accountNumber}</div>
                          </td>
                          <td style={styles.td}>{tx.transactionType}</td>
                          <td style={{ ...styles.td, fontWeight: 500 }}>
                            ${(tx.amount || 0).toLocaleString()}
                          </td>
                          <td style={styles.td}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                              <MapPin size={12} color="#94a3b8" />
                              {tx.location}
                            </div>
                          </td>
                          <td style={styles.td}>
                            <span style={styles.scoreBadge(tx.fraudScore || 0)}>
                              {tx.fraudScore || 0}
                            </span>
                          </td>
                          <td style={styles.td}>
                            {(() => {
                              const color = getRiskLevelColor(tx.riskLevel);
                              return (
                                <span style={{
                                  ...styles.badge,
                                  backgroundColor: color.bg,
                                  color: color.text,
                                  border: `1px solid ${color.border}`
                                }}>
                                  {tx.riskLevel || 'N/A'}
                                </span>
                              );
                            })()}
                          </td>
                          <td style={styles.td}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                              {getStatusIcon(tx.approvalStatus)}
                              <span style={{ fontSize: '0.75rem', fontWeight: 500 }}>{tx.approvalStatus}</span>
                            </div>
                          </td>
                          <td style={styles.td}>
                            <button
                              onClick={() => setExpandedRow(expandedRow === tx.id ? null : tx.id)}
                              style={{
                                background: 'none',
                                border: 'none',
                                cursor: 'pointer',
                                padding: '0.25rem',
                                color: '#94a3b8',
                                borderRadius: '0.25rem',
                              }}
                              title={expandedRow === tx.id ? 'Hide details' : 'View details'}
                            >
                              {expandedRow === tx.id ? <EyeOff size={16} /> : <Eye size={16} />}
                            </button>
                          </td>
                        </tr>

                        {expandedRow === tx.id && (
                          <tr>
                            <td colSpan="8" style={{ padding: 0 }}>
                              <div style={styles.expandableContent}>
                                <div style={styles.detailGrid}>
                                  <div style={styles.detailSection}>
                                    <div style={styles.detailTitle}>
                                      <BarChart3 size={16} />
                                      Fraud Analysis
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Fraud Detected:</span>
                                      <span style={{ ...styles.detailValue, color: tx.isFraud ? '#dc2626' : '#059669' }}>
                                        {tx.isFraud ? 'Yes' : 'No'}
                                      </span>
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Fraud Type:</span>
                                      <span style={styles.detailValue}>{tx.fraudType || 'N/A'}</span>
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Reason:</span>
                                      <span style={styles.detailValue}>{tx.fraudReason || 'No indicators detected'}</span>
                                    </div>
                                  </div>

                                  <div style={styles.detailSection}>
                                    <div style={styles.detailTitle}>
                                      <Server size={16} />
                                      Technical Details
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Device ID:</span>
                                      <span style={styles.detailValue}>{tx.deviceId || 'N/A'}</span>
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>IP Address:</span>
                                      <span style={styles.detailValue}>{tx.ipAddress || 'N/A'}</span>
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Merchant ID:</span>
                                      <span style={styles.detailValue}>{tx.merchantId || 'N/A'}</span>
                                    </div>
                                  </div>

                                  <div style={styles.detailSection}>
                                    <div style={styles.detailTitle}>
                                      <CreditCard size={16} />
                                      Transaction Info
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Transaction ID:</span>
                                      <span style={styles.detailValue}>{tx.id}</span>
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Success Status:</span>
                                      <span style={{ ...styles.detailValue, color: tx.successStatus ? '#059669' : '#dc2626' }}>
                                        {tx.successStatus ? 'Successful' : 'Failed'}
                                      </span>
                                    </div>
                                    <div style={styles.detailItem}>
                                      <span style={styles.detailLabel}>Timestamp:</span>
                                      <span style={styles.detailValue}>
                                        {new Date(tx.timestamp || Date.now()).toLocaleString()}
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </td>
                          </tr>
                        )}
                      </React.Fragment>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Footer */}
            <div style={styles.footer}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  Showing <strong>{filteredTransactions.length}</strong> of <strong>{transactions.length}</strong> transactions
                </div>
                <div>
                  Last updated: {new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </div>
              </div>
            </div>
          </>
        )}

        {/* Metrics Tab Content */}
        {activeTab === 'metrics' && renderMetricsTab()}

        {/* Testing Tab Content */}
        {activeTab === 'testing' && renderTestingTab()}
      </div>
    </div>
  );
}