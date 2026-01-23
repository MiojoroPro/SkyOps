-- =========================
-- CLASSE_SIEGE (classes dynamiques)
-- =========================
INSERT INTO classe_siege (code, libelle) VALUES
('ECO', 'Economique'),
('PRE', 'Premiere'),
('PRM', 'Premium');

-- =========================
-- CATEGORIE_AGE (categories de passagers)
-- =========================
INSERT INTO categorie_age (libelle, age_min, age_max) VALUES
('Adulte', 13, 120),
('Enfant', 2, 12),
('Bebe', 0, 1);

-- =========================
-- REMISE_CLASSE_CATEGORIE (pourcentage du tarif adulte par classe et categorie)
-- Exemple: Enfant en Eco = 80%, Enfant en Premiere = 70%
-- =========================
-- Economique (id_classe=1)
INSERT INTO remise_classe_categorie (id_classe, id_categorie, pourcentage) VALUES
(1, 1, 100.00),  -- Adulte Eco = 100%
(1, 2, 80.00),   -- Enfant Eco = 80%
(1, 3, 15.00);   -- Bebe Eco = 15%

-- Premiere (id_classe=2)
INSERT INTO remise_classe_categorie (id_classe, id_categorie, pourcentage) VALUES
(2, 1, 100.00),  -- Adulte Premiere = 100%
(2, 2, 70.00),   -- Enfant Premiere = 70%
(2, 3, 10.00);   -- Bebe Premiere = 10%

-- Premium (id_classe=3)
INSERT INTO remise_classe_categorie (id_classe, id_categorie, pourcentage) VALUES
(3, 1, 100.00),  -- Adulte Premium = 100%
(3, 2, 60.00),   -- Enfant Premium = 60%
(3, 3, 5.00);    -- Bebe Premium = 5%

-- =========================
-- UTILISATEUR
-- =========================
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
('Admin', 'System', 'admin@aeromanager.com', 'admin', 'ADMIN'),
('Rakoto', 'Jean', 'jean.rakoto@gmail.com', 'client', 'CLIENT'),
('Rabe', 'Sophie', 'sophie.rabe@gmail.com', 'client', 'CLIENT'),
('Kamel', 'Rinah', 'kamel.rinah@gmail.com', 'client', 'CLIENT'),
('Andria', 'Patrick', 'patrick.andria@gmail.com', 'client', 'CLIENT');

-- =========================
-- COMPAGNIE
-- =========================
INSERT INTO compagnie (nom, code_iata, code_icao, pays) VALUES
('Madagascar Airlines', 'MD', 'MDG', 'Madagascar'),
('Air France', 'AF', 'AFR', 'France'),
('Air Mauritius', 'MK', 'MAU', 'Maurice');

-- =========================
-- AEROPORT
-- =========================
INSERT INTO aeroport (nom, ville, pays, code_iata) VALUES
('Ivato International Airport', 'Antananarivo', 'Madagascar', 'TNR'),
('Charles de Gaulle', 'Paris', 'France', 'CDG'),
('Sir Seewoosagur Ramgoolam', 'Port-Louis', 'Maurice', 'MRU'),
('Fascene Airport', 'Nosy Be', 'Madagascar', 'NOS');

-- =========================
-- AVION (sans colonnes capacite)
-- =========================
INSERT INTO avion (modele, statut, id_compagnie) VALUES
('ATR 045', 'DISPONIBLE', 1);
('Boeing 737', 'DISPONIBLE', 1),
('Airbus A350', 'DISPONIBLE', 2),
('ATR 72', 'DISPONIBLE', 1),
('Boeing 777', 'MAINTENANCE', 2);

-- =========================
-- AVION_CLASSE (capacite par classe pour chaque avion)
-- =========================
-- Airbus A320 (id=1)
INSERT INTO avion_classe (id_avion, id_classe, capacite) VALUES
(1, 1, 150),
(1, 2, 30),
(1, 3, 20);

-- Boeing 737 (id=2)
INSERT INTO avion_classe (id_avion, id_classe, capacite) VALUES
(2, 1, 130),
(2, 2, 30),
(2, 3, 10);

-- Airbus A350 (id=3)
INSERT INTO avion_classe (id_avion, id_classe, capacite) VALUES
(3, 1, 260),
(3, 2, 40),
(3, 3, 30),
(3, 4, 20);

-- ATR 72 (id=4)
INSERT INTO avion_classe (id_avion, id_classe, capacite) VALUES
(4, 1, 68),
(4, 2, 8);

-- Boeing 777 (id=5)
INSERT INTO avion_classe (id_avion, id_classe, capacite) VALUES
(5, 1, 300),
(5, 2, 50),
(5, 3, 40),
(5, 4, 30);

-- =========================
-- VOL (ligne aerienne)
-- =========================
INSERT INTO vol (numero_vol, id_compagnie, id_aeroport_depart, id_aeroport_arrivee) VALUES
('MD001', 1, 1, 2),
('MD002', 1, 1, 3),
('MD003', 1, 1, 4),
('AF101', 2, 2, 1),
('MK501', 3, 3, 1);

-- =========================
-- VOL_DETAIL (execution de vol planifiee)
-- =========================
INSERT INTO vol_detail (date_heure_depart, date_heure_arrivee, statut, id_vol, id_avion) VALUES
('2026-01-20 08:00:00', '2026-01-20 20:00:00', 'PROGRAMME', 1, 1),
('2026-01-21 08:00:00', '2026-01-21 20:00:00', 'PROGRAMME', 1, 1),
('2026-01-22 18:00:00', '2026-01-23 06:00:00', 'PROGRAMME', 1, 2),
('2026-01-20 09:00:00', '2026-01-20 12:00:00', 'PROGRAMME', 2, 2),
('2026-01-21 09:00:00', '2026-01-21 12:00:00', 'PROGRAMME', 2, 2),
('2026-01-20 06:00:00', '2026-01-20 07:00:00', 'PROGRAMME', 3, 4),
('2026-01-21 06:00:00', '2026-01-21 07:00:00', 'PROGRAMME', 3, 4),
('2026-01-22 22:00:00', '2026-01-23 10:00:00', 'PROGRAMME', 4, 3),
('2026-01-23 14:00:00', '2026-01-23 17:00:00', 'PROGRAMME', 5, 2);

-- =========================
-- VOL_CLASSE (places restantes par classe pour chaque vol_detail)
-- =========================
INSERT INTO vol_classe (id_vol_detail, id_classe, places_restantes) VALUES
(1, 1, 148), (1, 2, 29), (1, 3, 20),
(2, 1, 150), (2, 2, 30), (2, 3, 20),
(3, 1, 130), (3, 2, 30), (3, 3, 10),
(4, 1, 129), (4, 2, 30), (4, 3, 10),
(5, 1, 130), (5, 2, 30), (5, 3, 10),
(6, 1, 68), (6, 2, 8),
(7, 1, 68), (7, 2, 8),
(8, 1, 260), (8, 2, 40), (8, 3, 30), (8, 4, 20),
(9, 1, 130), (9, 2, 30), (9, 3, 10);

-- =========================
-- PRIX_CLASSE_AGE (tarif adulte par classe pour chaque vol_detail)
-- Autres categories = pourcentage du tarif adulte
-- =========================
-- Vol MD001 du 20/01 (id_vol_detail=1)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(1, 1, 1, 1200000.00),
(1, 2, 1, 2200000.00),
(1, 3, 1, 3500000.00);

-- Vol MD001 du 21/01 (id_vol_detail=2)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(2, 1, 1, 1200000.00),
(2, 2, 1, 2200000.00),
(2, 3, 1, 3500000.00);

-- Vol MD001 du 22/01 soir (id_vol_detail=3)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(3, 1, 1, 1250000.00),
(3, 2, 1, 2300000.00),
(3, 3, 1, 3600000.00);

-- Vol MD002 du 20/01 (id_vol_detail=4)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(4, 1, 1, 600000.00),
(4, 2, 1, 1200000.00),
(4, 3, 1, 1800000.00);

-- Vol MD002 du 21/01 (id_vol_detail=5)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(5, 1, 1, 600000.00),
(5, 2, 1, 1200000.00),
(5, 3, 1, 1800000.00);

-- Vol MD003 du 20/01 (id_vol_detail=6)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(6, 1, 1, 350000.00),
(6, 2, 1, 700000.00);

-- Vol MD003 du 21/01 (id_vol_detail=7)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(7, 1, 1, 350000.00),
(7, 2, 1, 700000.00);

-- Vol AF101 du 22/01 (id_vol_detail=8)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(8, 1, 1, 1300000.00),
(8, 2, 1, 2500000.00),
(8, 3, 1, 4000000.00),
(8, 4, 1, 6000000.00);

-- Vol MK501 du 23/01 (id_vol_detail=9)
INSERT INTO prix_classe_age (id_vol_detail, id_classe, id_categorie, prix) VALUES
(9, 1, 1, 650000.00),
(9, 2, 1, 1300000.00),
(9, 3, 1, 2000000.00);

-- =========================
-- RESERVATION (avec FK vers classe_siege et categorie_age)
-- =========================
INSERT INTO reservation (date_reservation, numero_reservation, statut, id_utilisateur, id_vol_detail, id_classe, id_categorie) VALUES
(CURRENT_TIMESTAMP, 'RES-001', 'CONFIRMEE', 2, 1, 1, 1),
(CURRENT_TIMESTAMP, 'RES-002', 'CONFIRMEE', 2, 1, 1, 2),
(CURRENT_TIMESTAMP, 'RES-003', 'EN_ATTENTE', 3, 1, 2, 1),
(CURRENT_TIMESTAMP, 'RES-004', 'CONFIRMEE', 4, 4, 1, 1),
(CURRENT_TIMESTAMP, 'RES-005', 'EN_ATTENTE', 5, 8, 3, 1);

-- =========================
-- PAIEMENT
-- =========================
INSERT INTO paiement (date_paiement, montant, statut, id_reservation) VALUES
(CURRENT_TIMESTAMP, 1200000.00, 'PAYE', 1),
(CURRENT_TIMESTAMP, 900000.00, 'PAYE', 2),
(NULL, 2200000.00, 'NON_PAYE', 3),
(CURRENT_TIMESTAMP, 600000.00, 'PAYE', 4),
(NULL, 4000000.00, 'NON_PAYE', 5);


UPDATE prix_classe_age 
SET prix = 1500000.00 
WHERE id_classe = 1 AND id_categorie = 1;