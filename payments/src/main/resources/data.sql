INSERT INTO payment_entity (sender_account, receiver_account, amount, currency, status, created_at, notes)
VALUES ('ACC1001', 'ACC2001', 500, 'USD', 'COMPLETED', CURRENT_TIMESTAMP, 'Initial payment');

INSERT INTO payment_entity (sender_account, receiver_account, amount, currency, status, created_at, notes)
VALUES ('ACC1002', 'ACC2002', 1250, 'EUR', 'PENDING', CURRENT_TIMESTAMP, 'Monthly subscription');

INSERT INTO payment_entity (sender_account, receiver_account, amount, currency, status, created_at, notes)
VALUES ('ACC1003', 'ACC2003', 75, 'JOD', 'COMPLETED', CURRENT_TIMESTAMP, 'Service fee');