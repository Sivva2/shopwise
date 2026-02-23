-- ============================================
-- ShopWise - Script de création de la base de données
-- Modules : Clients, Fidélisation, Rendez-vous
-- ============================================

-- Suppression des tables existantes (ordre inverse des dépendances)
DROP TABLE IF EXISTS point_transaction;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS service;
DROP TABLE IF EXISTS client;

-- ============================================
-- TABLE : client
-- Description : Gestion des fiches clients
-- ============================================
CREATE TABLE client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    loyalty_points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Index pour les recherches fréquentes
CREATE INDEX idx_client_email ON client(email);
CREATE INDEX idx_client_last_name ON client(last_name);

-- ============================================
-- TABLE : service
-- Description : Services proposés par le commerce
-- ============================================
CREATE TABLE service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 30,
    points_awarded INT NOT NULL DEFAULT 10,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE : appointment
-- Description : Gestion des rendez-vous
-- ============================================
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_service FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE RESTRICT
);

-- Index pour les filtres fréquents
CREATE INDEX idx_appointment_date ON appointment(appointment_date);
CREATE INDEX idx_appointment_status ON appointment(status);
CREATE INDEX idx_appointment_client ON appointment(client_id);

-- ============================================
-- TABLE : point_transaction
-- Description : Historique des transactions de points fidélité
-- ============================================
CREATE TABLE point_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    appointment_id BIGINT,
    points INT NOT NULL,
    transaction_type ENUM('EARNED', 'REDEEMED', 'ADJUSTMENT') NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_transaction_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE SET NULL
);

-- Index pour consultation historique
CREATE INDEX idx_transaction_client ON point_transaction(client_id);
CREATE INDEX idx_transaction_date ON point_transaction(created_at);

-- ============================================
-- DONNÉES DE TEST
-- ============================================

-- Insertion des services
INSERT INTO service (name, description, duration_minutes, points_awarded, active) VALUES
('Consultation standard', 'Consultation de base avec le commerçant', 30, 10, TRUE),
('Consultation premium', 'Consultation approfondie avec conseils personnalisés', 60, 25, TRUE),
('Retrait commande', 'Retrait de commande Click & Collect', 15, 5, TRUE),
('Livraison à domicile', 'Préparation pour livraison à domicile', 45, 15, TRUE),
('Atelier découverte', 'Atelier de découverte des produits', 90, 50, TRUE);

-- Insertion des clients
INSERT INTO client (first_name, last_name, email, phone, loyalty_points) VALUES
('Marie', 'Dupont', 'marie.dupont@email.com', '0612345678', 150),
('Jean', 'Martin', 'jean.martin@email.com', '0623456789', 75),
('Sophie', 'Bernard', 'sophie.bernard@email.com', '0634567890', 200),
('Pierre', 'Petit', 'pierre.petit@email.com', '0645678901', 30),
('Isabelle', 'Durand', 'isabelle.durand@email.com', '0656789012', 0),
('Luc', 'Moreau', 'luc.moreau@email.com', '0667890123', 125),
('Emma', 'Laurent', 'emma.laurent@email.com', '0678901234', 45),
('Thomas', 'Simon', 'thomas.simon@email.com', '0689012345', 300);

-- Insertion des rendez-vous
INSERT INTO appointment (client_id, service_id, appointment_date, appointment_time, status, notes) VALUES
-- Rendez-vous passés (honorés)
(1, 1, '2025-01-15', '09:00:00', 'COMPLETED', 'Client régulier'),
(1, 2, '2025-01-22', '14:00:00', 'COMPLETED', NULL),
(2, 1, '2025-01-18', '10:30:00', 'COMPLETED', 'Première visite'),
(3, 3, '2025-01-20', '11:00:00', 'COMPLETED', 'Commande n°1234'),
(4, 1, '2025-01-25', '15:00:00', 'COMPLETED', NULL),

-- Rendez-vous passés (annulés)
(2, 2, '2025-01-19', '16:00:00', 'CANCELLED', 'Client indisponible'),
(5, 1, '2025-01-21', '09:30:00', 'CANCELLED', 'Annulation client'),

-- Rendez-vous à venir (planifiés)
(1, 5, '2025-02-28', '10:00:00', 'SCHEDULED', 'Atelier spécial'),
(3, 1, '2025-03-01', '14:30:00', 'SCHEDULED', NULL),
(6, 2, '2025-03-02', '11:00:00', 'SCHEDULED', 'Nouveau client fidélisé'),
(7, 3, '2025-03-03', '09:00:00', 'SCHEDULED', 'Click & Collect'),
(8, 4, '2025-03-04', '16:00:00', 'SCHEDULED', 'Livraison programmée');

-- Insertion des transactions de points
INSERT INTO point_transaction (client_id, appointment_id, points, transaction_type, description) VALUES
-- Points gagnés lors de rendez-vous honorés
(1, 1, 10, 'EARNED', 'Points gagnés - Consultation standard'),
(1, 2, 25, 'EARNED', 'Points gagnés - Consultation premium'),
(2, 3, 10, 'EARNED', 'Points gagnés - Consultation standard'),
(3, 4, 5, 'EARNED', 'Points gagnés - Retrait commande'),
(4, 5, 10, 'EARNED', 'Points gagnés - Consultation standard'),

-- Ajustements et utilisations
(1, NULL, -50, 'REDEEMED', 'Utilisation pour réduction'),
(3, NULL, 100, 'ADJUSTMENT', 'Bonus de bienvenue'),
(6, NULL, 50, 'ADJUSTMENT', 'Offre promotionnelle'),

-- Historique plus ancien pour Marie (id=1)
(1, NULL, 100, 'EARNED', 'Points historiques'),
(1, NULL, -35, 'REDEEMED', 'Réduction appliquée');

-- ============================================
-- VUES UTILITAIRES (optionnel)
-- ============================================

-- Vue pour afficher les rendez-vous avec détails client et service
CREATE OR REPLACE VIEW v_appointment_details AS
SELECT 
    a.id AS appointment_id,
    a.appointment_date,
    a.appointment_time,
    a.status,
    a.notes,
    c.id AS client_id,
    CONCAT(c.first_name, ' ', c.last_name) AS client_name,
    c.email AS client_email,
    c.phone AS client_phone,
    s.id AS service_id,
    s.name AS service_name,
    s.duration_minutes,
    s.points_awarded
FROM appointment a
JOIN client c ON a.client_id = c.id
JOIN service s ON a.service_id = s.id;

-- Vue pour le solde et historique des points par client
CREATE OR REPLACE VIEW v_client_loyalty AS
SELECT 
    c.id AS client_id,
    CONCAT(c.first_name, ' ', c.last_name) AS client_name,
    c.email,
    c.loyalty_points AS current_balance,
    COUNT(pt.id) AS total_transactions,
    COALESCE(SUM(CASE WHEN pt.transaction_type = 'EARNED' THEN pt.points ELSE 0 END), 0) AS total_earned,
    COALESCE(SUM(CASE WHEN pt.transaction_type = 'REDEEMED' THEN ABS(pt.points) ELSE 0 END), 0) AS total_redeemed
FROM client c
LEFT JOIN point_transaction pt ON c.id = pt.client_id
GROUP BY c.id, c.first_name, c.last_name, c.email, c.loyalty_points;
