INSERT IGNORE INTO appointment_status (name) VALUES ('PENDING');
INSERT IGNORE INTO appointment_status (name) VALUES ('CONFIRMED');
INSERT IGNORE INTO appointment_status (name) VALUES ('CANCELLED');
INSERT IGNORE INTO appointment_status (name) VALUES ('COMPLETED');

-- Catalogo de servicios
INSERT IGNORE INTO medical_services (name, price) VALUES ('Consulta General', 15000.0);
INSERT IGNORE INTO medical_services (name, price) VALUES ('Vacunación', 12000.0);
INSERT IGNORE INTO medical_services (name, price) VALUES ('Urgencia', 35000.0);
INSERT IGNORE INTO medical_services (name, price) VALUES ('Cirugía Menor', 80000.0);
INSERT IGNORE INTO medical_services (name, price) VALUES ('Ecografía', 30000.0);
INSERT IGNORE INTO medical_services (name, price) VALUES ('Examen de Sangre', 25000.0);
INSERT IGNORE INTO medical_services (name, price) VALUES ('Peluquería Canina', 20000.0);