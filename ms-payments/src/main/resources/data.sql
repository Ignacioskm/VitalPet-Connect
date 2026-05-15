-- Estados de Pago
INSERT IGNORE INTO payment_status (name) VALUES ('PENDING');
INSERT IGNORE INTO payment_status (name) VALUES ('PAID');
INSERT IGNORE INTO payment_status (name) VALUES ('REFUNDED');

-- Métodos de Pago
INSERT IGNORE INTO payment_method (name) VALUES ('CASH');
INSERT IGNORE INTO payment_method (name) VALUES ('CARD');
INSERT IGNORE INTO payment_method (name) VALUES ('TRANSFER');