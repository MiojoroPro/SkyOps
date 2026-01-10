INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
('Admin', 'System', 'admin@aeromanager.com', 'admin', 'ADMIN'),
('Rakoto', 'Jean', 'jean.rakoto@gmail.com', 'client', 'CLIENT'),
('Rabe', 'Sophie', 'sophie.rabe@gmail.com', 'client', 'CLIENT');

INSERT INTO compagnie (nom, code_iata, code_icao, pays) VALUES
('Madagascar Airlines', 'MD', 'MDG', 'Madagascar'),
('Air France', 'AF', 'AFR', 'France');

INSERT INTO aeroport (nom, ville, pays, code_iata) VALUES
('Ivato International Airport', 'Antananarivo', 'Madagascar', 'TNR'),
('Charles de Gaulle', 'Paris', 'France', 'CDG'),
('Sir Seewoosagur Ramgoolam', 'Port-Louis', 'Maurice', 'MRU');

INSERT INTO avion (modele, capacite, statut, id_compagnie) VALUES
('Airbus A320', 180, 'DISPONIBLE', 1),
('Boeing 737', 160, 'DISPONIBLE', 1),
('Airbus A350', 300, 'DISPONIBLE', 2);

INSERT INTO vol (numero_vol, prix_base, id_compagnie, id_aeroport_depart, id_aeroport_arrivee) VALUES
('MD001', 1200.00, 1, 1, 2), -- TNR -> CDG
('MD002', 600.00, 1, 1, 3),  -- TNR -> MRU
('AF101', 1300.00, 2, 2, 1); -- CDG -> TNR

INSERT INTO vol_detail (
    date_heure_depart,
    date_heure_arrivee,
    statut,
    id_vol,
    id_avion
) VALUES
-- MD001 : plusieurs fois par jour, avions différents
('2026-01-10 08:00:00', '2026-01-10 20:00:00', 'PROGRAMME', 1, 1),
('2026-01-10 18:00:00', '2026-01-11 06:00:00', 'PROGRAMME', 1, 2),

-- MD001 le lendemain
('2026-01-11 08:00:00', '2026-01-11 20:00:00', 'PROGRAMME', 1, 1),

-- MD002
('2026-01-10 09:00:00', '2026-01-10 12:00:00', 'PROGRAMME', 2, 2),

-- AF101
('2026-01-12 22:00:00', '2026-01-13 10:00:00', 'PROGRAMME', 3, 3);


INSERT INTO reservation (
    date_reservation,
    numero_reservation,
    statut,
    id_utilisateur,
    id_vol_detail
) VALUES
(CURRENT_TIMESTAMP, 'RES-001', 'CONFIRMEE', 2, 1),
(CURRENT_TIMESTAMP, 'RES-002', 'EN_ATTENTE', 3, 2),
(CURRENT_TIMESTAMP, 'RES-003', 'CONFIRMEE', 2, 4);


INSERT INTO paiement (
    date_paiement,
    montant,
    statut,
    id_reservation
) VALUES
(CURRENT_TIMESTAMP, 1200.00, 'PAYE', 1),
(NULL, 1200.00, 'NON_PAYE', 2),
(CURRENT_TIMESTAMP, 600.00, 'PAYE', 3);
