-- =========================
-- UTILISATEUR
-- =========================
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
('Admin', 'System', 'admin@aeromanager.com', 'admin', 'ADMIN'),
('Rakoto', 'Jean', 'jean.rakoto@gmail.com', 'client', 'CLIENT'),
('Rabe', 'Sophie', 'sophie.rabe@gmail.com', 'client', 'CLIENT');


INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
('kamel', 'Rinah', 'kamel.rinah@gmail.com', 'client', 'CLIENT');
-- =========================
-- COMPAGNIE
-- =========================
INSERT INTO compagnie (nom, code_iata, code_icao, pays) VALUES
('Madagascar Airlines', 'MD', 'MDG', 'Madagascar'),
('Air France', 'AF', 'AFR', 'France');

-- =========================
-- AEROPORT
-- =========================
INSERT INTO aeroport (nom, ville, pays, code_iata) VALUES
('Ivato International Airport', 'Antananarivo', 'Madagascar', 'TNR'),
('Charles de Gaulle', 'Paris', 'France', 'CDG'),
('Sir Seewoosagur Ramgoolam', 'Port-Louis', 'Maurice', 'MRU');

-- =========================
-- AVION
-- capacite_economique + capacite_premiere
-- =========================
INSERT INTO avion (
    modele,
    capacite_economique,
    capacite_premiere,
    statut,
    id_compagnie
) VALUES
('Airbus A320', 150, 30, 'DISPONIBLE', 1),
('Boeing 737', 130, 30, 'DISPONIBLE', 1),
('Airbus A350', 260, 40, 'DISPONIBLE', 2);

-- =========================
-- VOL (ligne aérienne)
-- =========================
INSERT INTO vol (
    numero_vol,
    id_compagnie,
    id_aeroport_depart,
    id_aeroport_arrivee
) VALUES
('MD001', 1, 1, 2), -- TNR -> CDG
('MD002', 1, 1, 3), -- TNR -> MRU
('AF101', 2, 2, 1); -- CDG -> TNR

-- =========================
-- VOL_DETAIL (VOL PLANIFIÉ)
-- prix + places par classe
-- =========================
INSERT INTO vol_detail (
    date_heure_depart,
    date_heure_arrivee,
    prix_economique,
    prix_premiere,
    places_eco_restantes,
    places_premiere_restantes,
    statut,
    id_vol,
    id_avion
) VALUES
-- MD001
('2026-01-10 08:00:00', '2026-01-10 20:00:00',
 1200.00, 2200.00,
 150, 30,
 'PROGRAMME', 1, 1),

('2026-01-10 18:00:00', '2026-01-11 06:00:00',
 1250.00, 2300.00,
 130, 30,
 'PROGRAMME', 1, 2),

('2026-01-11 08:00:00', '2026-01-11 20:00:00',
 1200.00, 2200.00,
 150, 30,
 'PROGRAMME', 1, 1),

-- MD002
('2026-01-10 09:00:00', '2026-01-10 12:00:00',
 600.00, 1200.00,
 130, 30,
 'PROGRAMME', 2, 2),

-- AF101
('2026-01-12 22:00:00', '2026-01-13 10:00:00',
 1300.00, 2500.00,
 260, 40,
 'PROGRAMME', 3, 3);

-- =========================
-- RESERVATION
-- classe obligatoire
-- =========================
INSERT INTO reservation (
    date_reservation,
    numero_reservation,
    classe,
    statut,
    id_utilisateur,
    id_vol_detail
) VALUES
(CURRENT_TIMESTAMP, 'RES-001', 'ECONOMIQUE', 'CONFIRMEE', 2, 1),
(CURRENT_TIMESTAMP, 'RES-002', 'PREMIERE',   'EN_ATTENTE', 3, 2),
(CURRENT_TIMESTAMP, 'RES-003', 'ECONOMIQUE', 'CONFIRMEE', 2, 4);

-- =========================
-- PAIEMENT
-- =========================
INSERT INTO paiement (
    date_paiement,
    montant,
    statut,
    id_reservation
) VALUES
(CURRENT_TIMESTAMP, 1200.00, 'PAYE', 1),
(NULL, 2300.00, 'NON_PAYE', 2),
(CURRENT_TIMESTAMP, 600.00, 'PAYE', 3);
