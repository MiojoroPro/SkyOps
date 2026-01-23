-- =========================
-- DONNÉES PUBLICITÉS
-- =========================

-- Sociétés annonceurs
INSERT INTO societe_annonceur (nom, email, telephone) VALUES 
('Vaniala', 'contact@vaniala.mg', '+261 34 00 000 01'),
('Lewis', 'info@lewis.mg', '+261 34 00 000 02');

INSERT INTO societe_annonceur (nom, email, telephone) VALUES 
('Socobis', 'info@socobis.mg', '+261 34 10 020 02');

INSERT INTO societe_annonceur (nom, email, telephone) VALUES 
('Jejoo', 'info@jejooo.mg', '+261 14 19 120 32');

-- Publicités
INSERT INTO publicite (titre, duree_seconde, description, id_societe) VALUES 
('Vaniala - Parfum Naturel', 30, 'Publicité pour les parfums naturels Vaniala', 1),
('Lewis - Mode Homme', 45, 'Collection automne-hiver Lewis', 2),
('Socobis - Gasy Ka Tsara', 45, '18 Petit Beure ', 3);

-- Tarif publicitaire: 400 000 Ar / diffusion
INSERT INTO tarif_publicitaire (prix_unitaire, date_debut, date_fin) VALUES 
(400000.00, '2025-01-01', NULL);

-- Diffusions de décembre 2025
-- Note: id_avion doit correspondre à un avion existant dans votre base de données
-- Remplacez les valeurs id_avion (1 et 2) par des IDs d'avions existants

-- Vaniala: 20 diffusions sur l'avion ID 1
INSERT INTO diffusion_publicitaire (mois, annee, nombre_diffusions, id_publicite, id_tarif, id_avion) VALUES 
(12, 2025, 20, 1, 1, 1);

-- Lewis: 10 diffusions sur l'avion ID 2
INSERT INTO diffusion_publicitaire (mois, annee, nombre_diffusions, id_publicite, id_tarif, id_avion) VALUES 
(12, 2025, 10, 2, 1, 2);

-- =========================
-- RÉSULTAT ATTENDU:
-- CA Décembre 2025 = (20 + 10) * 400 000 = 12 000 000 Ar
-- Vaniala: 20 * 400 000 = 8 000 000 Ar
-- Lewis: 10 * 400 000 = 4 000 000 Ar
-- =========================
