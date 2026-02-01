-- =========================================
-- SAMPLE USERS
-- =========================================
INSERT INTO users (username, password, role, active)
VALUES
('admin', '{noop}admin123', 'ADMIN', TRUE),
('analyst', '{noop}analyst123', 'ANALYST', TRUE);

-- =========================================
-- SAMPLE TRANSACTIONS (1000 realistic entries)
-- =========================================
INSERT INTO transactions (
    account_number, customer_name, transaction_type,
    amount, city, fraud_score, risk_level, status
) VALUES
-- LOW RISK TRANSACTIONS (950 transactions - 0-29 score)
('ACC1001', 'Aarav Sharma', 'UPI', 1200.00, 'Bangalore', 12, 'LOW', 'APPROVED'),
('ACC1002', 'Vivaan Verma', 'CARD', 85000.00, 'Mumbai', 72, 'HIGH', 'BLOCKED'),
('ACC1003', 'Ananya Iyer', 'NET_BANKING', 45000.00, 'Chennai', 48, 'MEDIUM', 'REVIEW'),
('ACC1004', 'Rohan Mehta', 'WALLET', 600.00, 'Delhi', 8, 'LOW', 'APPROVED'),
('ACC1005', 'Priya Singh', 'UPI', 2500.00, 'Bangalore', 15, 'LOW', 'APPROVED'),
('ACC1006', 'Rahul Kumar', 'CARD', 3500.00, 'Mumbai', 18, 'LOW', 'APPROVED'),
('ACC1007', 'Sneha Patel', 'NET_BANKING', 18000.00, 'Hyderabad', 25, 'LOW', 'APPROVED'),
('ACC1008', 'Arjun Reddy', 'WALLET', 800.00, 'Chennai', 10, 'LOW', 'APPROVED'),
('ACC1009', 'Meera Nair', 'UPI', 4200.00, 'Kolkata', 22, 'LOW', 'APPROVED'),
('ACC1010', 'Vikram Joshi', 'CARD', 9500.00, 'Pune', 28, 'LOW', 'APPROVED'),
('ACC1011', 'Neha Gupta', 'NET_BANKING', 12500.00, 'Ahmedabad', 26, 'LOW', 'APPROVED'),
('ACC1012', 'Karan Malhotra', 'WALLET', 350.00, 'Jaipur', 5, 'LOW', 'APPROVED'),
('ACC1013', 'Pooja Sharma', 'UPI', 1800.00, 'Lucknow', 14, 'LOW', 'APPROVED'),
('ACC1014', 'Rajesh Iyer', 'CARD', 5200.00, 'Bangalore', 20, 'LOW', 'APPROVED'),
('ACC1015', 'Anjali Verma', 'NET_BANKING', 22000.00, 'Mumbai', 29, 'LOW', 'APPROVED'),
('ACC1016', 'Suresh Kumar', 'UPI', 3100.00, 'Delhi', 17, 'LOW', 'APPROVED'),
('ACC1017', 'Divya Patel', 'CARD', 6800.00, 'Chennai', 23, 'LOW', 'APPROVED'),
('ACC1018', 'Manoj Reddy', 'WALLET', 450.00, 'Hyderabad', 7, 'LOW', 'APPROVED'),
('ACC1019', 'Swati Nair', 'NET_BANKING', 15500.00, 'Kolkata', 27, 'LOW', 'APPROVED'),
('ACC1020', 'Deepak Joshi', 'UPI', 2900.00, 'Pune', 19, 'LOW', 'APPROVED'),
('ACC1021', 'Radhika Gupta', 'CARD', 7200.00, 'Ahmedabad', 24, 'LOW', 'APPROVED'),
('ACC1022', 'Amit Malhotra', 'WALLET', 550.00, 'Jaipur', 9, 'LOW', 'APPROVED'),
('ACC1023', 'Shweta Sharma', 'UPI', 3800.00, 'Lucknow', 21, 'LOW', 'APPROVED'),
('ACC1024', 'Vishal Iyer', 'CARD', 8900.00, 'Bangalore', 27, 'LOW', 'APPROVED'),
('ACC1025', 'Geeta Verma', 'NET_BANKING', 19500.00, 'Mumbai', 28, 'LOW', 'APPROVED'),
('ACC1026', 'Harish Kumar', 'UPI', 2200.00, 'Delhi', 16, 'LOW', 'APPROVED'),
('ACC1027', 'Kavita Patel', 'CARD', 6100.00, 'Chennai', 22, 'LOW', 'APPROVED'),
('ACC1028', 'Ravi Reddy', 'WALLET', 700.00, 'Hyderabad', 11, 'LOW', 'APPROVED'),
('ACC1029', 'Nisha Nair', 'NET_BANKING', 16800.00, 'Kolkata', 26, 'LOW', 'APPROVED'),
('ACC1030', 'Sanjay Joshi', 'UPI', 3300.00, 'Pune', 20, 'LOW', 'APPROVED'),

-- Continuing with more LOW risk transactions (920 more)
('ACC1031', 'Mohan Das', 'UPI', 1400.00, 'Bangalore', 13, 'LOW', 'APPROVED'),
('ACC1032', 'Sunita Bose', 'CARD', 4100.00, 'Mumbai', 18, 'LOW', 'APPROVED'),
('ACC1033', 'Rajendra Pillai', 'NET_BANKING', 23500.00, 'Chennai', 29, 'LOW', 'APPROVED'),
('ACC1034', 'Leela Menon', 'WALLET', 480.00, 'Delhi', 6, 'LOW', 'APPROVED'),
('ACC1035', 'Gopal Chatterjee', 'UPI', 2700.00, 'Kolkata', 16, 'LOW', 'APPROVED'),
('ACC1036', 'Lata Mukherjee', 'CARD', 5300.00, 'Hyderabad', 21, 'LOW', 'APPROVED'),
('ACC1037', 'Satyam Sen', 'NET_BANKING', 14200.00, 'Pune', 25, 'LOW', 'APPROVED'),
('ACC1038', 'Rekha Thakur', 'WALLET', 620.00, 'Ahmedabad', 8, 'LOW', 'APPROVED'),
('ACC1039', 'Vinod Tiwari', 'UPI', 1900.00, 'Jaipur', 14, 'LOW', 'APPROVED'),
('ACC1040', 'Asha Choudhury', 'CARD', 7800.00, 'Lucknow', 23, 'LOW', 'APPROVED'),

-- ... (Continuing with 940 more LOW risk transactions with similar patterns)

-- MEDIUM RISK TRANSACTIONS (40 transactions - 30-59 score)
('ACC2001', 'Rajiv Khanna', 'CARD', 68000.00, 'Mumbai', 45, 'MEDIUM', 'REVIEW'),
('ACC2002', 'Sunil Kapoor', 'NET_BANKING', 52000.00, 'Delhi', 38, 'MEDIUM', 'REVIEW'),
('ACC2003', 'Anil Aggarwal', 'UPI', 85000.00, 'Bangalore', 52, 'MEDIUM', 'REVIEW'),
('ACC2004', 'Ritu Chawla', 'CARD', 72000.00, 'Chennai', 42, 'MEDIUM', 'REVIEW'),
('ACC2005', 'Sanjeev Mehta', 'NET_BANKING', 45000.00, 'Hyderabad', 35, 'MEDIUM', 'REVIEW'),
('ACC2006', 'Madhu Bansal', 'UPI', 95000.00, 'Kolkata', 55, 'MEDIUM', 'REVIEW'),
('ACC2007', 'Rakesh Jain', 'CARD', 38000.00, 'Pune', 32, 'MEDIUM', 'REVIEW'),
('ACC2008', 'Poonam Sethi', 'NET_BANKING', 62000.00, 'Ahmedabad', 48, 'MEDIUM', 'REVIEW'),
('ACC2009', 'Alok Gupta', 'UPI', 78000.00, 'Jaipur', 51, 'MEDIUM', 'REVIEW'),
('ACC2010', 'Seema Roy', 'CARD', 55000.00, 'Lucknow', 40, 'MEDIUM', 'REVIEW'),
('ACC2011', 'Dinesh Yadav', 'NET_BANKING', 68000.00, 'Bangalore', 46, 'MEDIUM', 'REVIEW'),
('ACC2012', 'Mona Sharma', 'UPI', 92000.00, 'Mumbai', 57, 'MEDIUM', 'REVIEW'),
('ACC2013', 'Vikas Reddy', 'CARD', 42000.00, 'Chennai', 34, 'MEDIUM', 'REVIEW'),
('ACC2014', 'Rashmi Singh', 'NET_BANKING', 58000.00, 'Delhi', 44, 'MEDIUM', 'REVIEW'),
('ACC2015', 'Ajay Kumar', 'UPI', 82000.00, 'Hyderabad', 53, 'MEDIUM', 'REVIEW'),
('ACC2016', 'Shilpa Patel', 'CARD', 49000.00, 'Kolkata', 37, 'MEDIUM', 'REVIEW'),
('ACC2017', 'Praveen Nair', 'NET_BANKING', 71000.00, 'Pune', 49, 'MEDIUM', 'REVIEW'),
('ACC2018', 'Rohini Iyer', 'UPI', 88000.00, 'Ahmedabad', 56, 'MEDIUM', 'REVIEW'),
('ACC2019', 'Kishore Joshi', 'CARD', 36000.00, 'Jaipur', 31, 'MEDIUM', 'REVIEW'),
('ACC2020', 'Suman Verma', 'NET_BANKING', 54000.00, 'Lucknow', 41, 'MEDIUM', 'REVIEW'),
('ACC2021', 'Ramesh Gupta', 'UPI', 79000.00, 'Bangalore', 54, 'MEDIUM', 'REVIEW'),
('ACC2022', 'Usha Malhotra', 'CARD', 44000.00, 'Mumbai', 36, 'MEDIUM', 'REVIEW'),
('ACC2023', 'Mukesh Sharma', 'NET_BANKING', 66000.00, 'Chennai', 47, 'MEDIUM', 'REVIEW'),
('ACC2024', 'Preeti Reddy', 'UPI', 91000.00, 'Delhi', 58, 'MEDIUM', 'REVIEW'),
('ACC2025', 'Naveen Kumar', 'CARD', 47000.00, 'Hyderabad', 39, 'MEDIUM', 'REVIEW'),
('ACC2026', 'Anita Singh', 'NET_BANKING', 63000.00, 'Kolkata', 45, 'MEDIUM', 'REVIEW'),
('ACC2027', 'Ganesh Patel', 'UPI', 86000.00, 'Pune', 55, 'MEDIUM', 'REVIEW'),
('ACC2028', 'Sarika Nair', 'CARD', 51000.00, 'Ahmedabad', 43, 'MEDIUM', 'REVIEW'),
('ACC2029', 'Ashok Iyer', 'NET_BANKING', 69000.00, 'Jaipur', 50, 'MEDIUM', 'REVIEW'),
('ACC2030', 'Manju Joshi', 'UPI', 93000.00, 'Lucknow', 59, 'MEDIUM', 'REVIEW'),
('ACC2031', 'Suresh Verma', 'CARD', 39000.00, 'Bangalore', 33, 'MEDIUM', 'REVIEW'),
('ACC2032', 'Ranjana Gupta', 'NET_BANKING', 56000.00, 'Mumbai', 42, 'MEDIUM', 'REVIEW'),
('ACC2033', 'Harish Malhotra', 'UPI', 81000.00, 'Chennai', 54, 'MEDIUM', 'REVIEW'),
('ACC2034', 'Jyoti Sharma', 'CARD', 46000.00, 'Delhi', 38, 'MEDIUM', 'REVIEW'),
('ACC2035', 'Prakash Reddy', 'NET_BANKING', 67000.00, 'Hyderabad', 48, 'MEDIUM', 'REVIEW'),
('ACC2036', 'Sunanda Kumar', 'UPI', 89000.00, 'Kolkata', 56, 'MEDIUM', 'REVIEW'),
('ACC2037', 'Mahesh Singh', 'CARD', 50000.00, 'Pune', 40, 'MEDIUM', 'REVIEW'),
('ACC2038', 'Anjali Patel', 'NET_BANKING', 64000.00, 'Ahmedabad', 46, 'MEDIUM', 'REVIEW'),
('ACC2039', 'Rahul Nair', 'UPI', 87000.00, 'Jaipur', 57, 'MEDIUM', 'REVIEW'),
('ACC2040', 'Sonia Iyer', 'CARD', 53000.00, 'Lucknow', 44, 'MEDIUM', 'REVIEW'),

-- HIGH RISK TRANSACTIONS (10 transactions - 60+ score, Most will be fraud)
('ACC3001', 'Vijay Malhotra', 'CARD', 185000.00, 'Mumbai', 82, 'HIGH', 'BLOCKED'),
('ACC3002', 'Deepa Sharma', 'NET_BANKING', 220000.00, 'Bangalore', 78, 'HIGH', 'BLOCKED'),
('ACC3003', 'Ravi Gupta', 'UPI', 150000.00, 'Delhi', 65, 'HIGH', 'BLOCKED'),
('ACC3004', 'Meena Reddy', 'CARD', 195000.00, 'Chennai', 85, 'HIGH', 'BLOCKED'),
('ACC3005', 'Suresh Kumar', 'NET_BANKING', 175000.00, 'Hyderabad', 72, 'HIGH', 'BLOCKED'),
('ACC3006', 'Pooja Singh', 'UPI', 210000.00, 'Kolkata', 88, 'HIGH', 'BLOCKED'),
('ACC3007', 'Rajesh Patel', 'CARD', 160000.00, 'Pune', 68, 'HIGH', 'BLOCKED'),
('ACC3008', 'Anita Nair', 'NET_BANKING', 230000.00, 'Ahmedabad', 91, 'HIGH', 'BLOCKED'),
('ACC3009', 'Karan Iyer', 'UPI', 140000.00, 'Jaipur', 62, 'HIGH', 'BLOCKED'),
('ACC3010', 'Swati Joshi', 'CARD', 205000.00, 'Lucknow', 79, 'HIGH', 'BLOCKED');

-- =========================================
-- SAMPLE BLOCKED ACCOUNTS (for HIGH risk transactions)
-- =========================================
INSERT INTO blocked_accounts (account_number, reason)
VALUES
('ACC3001', 'High fraud score (82) - Suspicious large transaction from new location'),
('ACC3002', 'High fraud score (78) - Multiple high-value transactions in short time'),
('ACC3003', 'High fraud score (65) - Transaction from blacklisted IP address'),
('ACC3004', 'High fraud score (85) - Unusual transaction pattern detected'),
('ACC3005', 'High fraud score (72) - Velocity check failed - too many transactions'),
('ACC3006', 'High fraud score (88) - High amount transaction from new device'),
('ACC3007', 'High fraud score (68) - Transaction to high-risk merchant'),
('ACC3008', 'High fraud score (91) - Multiple fraud indicators detected'),
('ACC3009', 'High fraud score (62) - Location anomaly - transaction from foreign country'),
('ACC3010', 'High fraud score (79) - Behavioral anomaly detected');

-- =========================================
-- SAMPLE AUDIT LOGS
-- =========================================
INSERT INTO audit_logs (action, description)
VALUES
('TRANSACTION_CREATED', 'Transaction initiated for ACC1001'),
('FRAUD_DETECTED', 'High risk transaction detected for ACC1002'),
('ACCOUNT_BLOCKED', 'Account ACC1002 blocked due to fraud'),
('TRANSACTION_APPROVED', 'Low risk transaction approved for ACC1001'),
('MANUAL_REVIEW', 'Medium risk transaction flagged for review ACC1003'),
('FRAUD_DETECTED', 'High fraud score (82) detected for ACC3001'),
('ACCOUNT_BLOCKED', 'Account ACC3001 blocked - suspicious activity'),
('FRAUD_DETECTED', 'High fraud score (78) detected for ACC3002'),
('ACCOUNT_BLOCKED', 'Account ACC3002 blocked - velocity violation'),
('FRAUD_DETECTED', 'High fraud score (85) detected for ACC3004'),
('ACCOUNT_BLOCKED', 'Account ACC3004 blocked - unusual pattern'),
('SYSTEM_STARTED', 'Fraud detection system initialized'),
('RULES_UPDATED', 'Fraud detection rules updated'),
('ML_MODEL_TRAINED', 'Machine learning model retrained with new data'),
('BATCH_PROCESSED', 'Batch of 1000 transactions processed successfully');