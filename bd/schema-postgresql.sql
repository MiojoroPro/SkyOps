-- ============================================
-- SCHEMA COMPLET - BASE DE DONNÉES AÉROPORT
-- ============================================
-- Base de données pour la gestion des vols, réservations et paiements
-- Conforme aux entités JPA du projet

-- DROP DATABASE IF EXISTS aero;
-- CREATE DATABASE aero;
-- \c aero;

-- =========================
-- TABLE UTILISATEUR
-- =========================
-- Gère les utilisateurs de l'application (admin, agent, client)
-- Implémente UserDetails pour Spring Security
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- ex: ROLE_ADMIN, ROLE_USER, ROLE_AGENT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_utilisateur_email ON utilisateur(email);
CREATE INDEX idx_utilisateur_role ON utilisateur(role);

-- =========================
-- TABLE COMPAGNIE
-- =========================
-- Compagnies aériennes exploitant les vols
CREATE TABLE compagnie (
    id_compagnie SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    code_iata VARCHAR(5) UNIQUE NOT NULL,
    code_icao VARCHAR(5) UNIQUE NOT NULL,
    pays VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_compagnie_code_iata ON compagnie(code_iata);
CREATE INDEX idx_compagnie_code_icao ON compagnie(code_icao);

-- =========================
-- TABLE AEROPORT
-- =========================
-- Aéroports de départ et d'arrivée
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    code_iata VARCHAR(5) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_aeroport_code_iata ON aeroport(code_iata);
CREATE INDEX idx_aeroport_ville ON aeroport(ville);

-- =========================
-- TABLE CLASSE_SIEGE
-- =========================
-- Types de sièges/classes disponibles (Économique, Business, Première)
CREATE TABLE classe_siege (
    id_classe SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,      -- ECONOMIQUE, PREMIERE, BUSINESS
    libelle VARCHAR(50) NOT NULL,          -- Libellé de la classe
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_classe_siege_code ON classe_siege(code);

-- =========================
-- TABLE CATEGORIE_AGE
-- =========================
-- Catégories d'âge pour la tarification
-- (Adulte: 18-100, Enfant: 2-17, Bébé: 0-1, etc.)
CREATE TABLE categorie_age (
    id_categorie SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL,          -- ex: Adulte, Enfant, Bébé
    age_min INT NOT NULL CHECK (age_min >= 0),
    age_max INT NOT NULL CHECK (age_max >= age_min),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categorie_age_libelle ON categorie_age(libelle);

-- =========================
-- TABLE AVION
-- =========================
-- Avions appartenant aux compagnies
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    modele VARCHAR(100) NOT NULL,
    statut VARCHAR(20) DEFAULT 'ACTIF',    -- ACTIF, EN_MAINTENANCE, RETIRÉ
    id_compagnie INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avion_compagnie FOREIGN KEY (id_compagnie) 
        REFERENCES compagnie(id_compagnie) ON DELETE RESTRICT
);

CREATE INDEX idx_avion_compagnie ON avion(id_compagnie);
CREATE INDEX idx_avion_statut ON avion(statut);

-- =========================
-- TABLE AVION_CLASSE
-- =========================
-- Capacité de chaque classe pour chaque avion
-- Table de jonction avec attribut: capacite
CREATE TABLE avion_classe (
    id_avion INT NOT NULL,
    id_classe INT NOT NULL,
    capacite INT NOT NULL CHECK (capacite > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_avion, id_classe),
    CONSTRAINT fk_avion_classe_avion FOREIGN KEY (id_avion) 
        REFERENCES avion(id_avion) ON DELETE CASCADE,
    CONSTRAINT fk_avion_classe_classe FOREIGN KEY (id_classe) 
        REFERENCES classe_siege(id_classe) ON DELETE RESTRICT
);

CREATE INDEX idx_avion_classe_avion ON avion_classe(id_avion);
CREATE INDEX idx_avion_classe_classe ON avion_classe(id_classe);

-- =========================
-- TABLE VOL
-- =========================
-- Définition générale des vols (itinéraire récurrent)
CREATE TABLE vol (
    id_vol SERIAL PRIMARY KEY,
    numero_vol VARCHAR(20) NOT NULL,
    id_compagnie INT NOT NULL,
    id_aeroport_depart INT NOT NULL,
    id_aeroport_arrivee INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vol_compagnie FOREIGN KEY (id_compagnie) 
        REFERENCES compagnie(id_compagnie) ON DELETE RESTRICT,
    CONSTRAINT fk_vol_aeroport_depart FOREIGN KEY (id_aeroport_depart) 
        REFERENCES aeroport(id_aeroport) ON DELETE RESTRICT,
    CONSTRAINT fk_vol_aeroport_arrivee FOREIGN KEY (id_aeroport_arrivee) 
        REFERENCES aeroport(id_aeroport) ON DELETE RESTRICT,
    CONSTRAINT chk_vol_diff_aeroport 
        CHECK (id_aeroport_depart <> id_aeroport_arrivee)
);

CREATE INDEX idx_vol_compagnie ON vol(id_compagnie);
CREATE INDEX idx_vol_numero ON vol(numero_vol);
CREATE INDEX idx_vol_aeroports ON vol(id_aeroport_depart, id_aeroport_arrivee);

-- =========================
-- TABLE VOL_DETAIL
-- =========================
-- Instance spécifique d'un vol (date, heure, avion assigné)
CREATE TABLE vol_detail (
    id_vol_detail SERIAL PRIMARY KEY,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    statut VARCHAR(20) DEFAULT 'PRÉVU',    -- PRÉVU, EN_COURS, ARRIVÉ, ANNULÉ, REPORTÉ
    id_vol INT NOT NULL,
    id_avion INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vol_detail_vol FOREIGN KEY (id_vol) 
        REFERENCES vol(id_vol) ON DELETE RESTRICT,
    CONSTRAINT fk_vol_detail_avion FOREIGN KEY (id_avion) 
        REFERENCES avion(id_avion) ON DELETE RESTRICT,
    CONSTRAINT chk_vol_detail_dates 
        CHECK (date_heure_arrivee > date_heure_depart)
);

CREATE INDEX idx_vol_detail_vol ON vol_detail(id_vol);
CREATE INDEX idx_vol_detail_avion ON vol_detail(id_avion);
CREATE INDEX idx_vol_detail_depart ON vol_detail(date_heure_depart);
CREATE INDEX idx_vol_detail_arrivee ON vol_detail(date_heure_arrivee);
CREATE INDEX idx_vol_detail_statut ON vol_detail(statut);

-- =========================
-- TABLE VOL_CLASSE
-- =========================
-- Places restantes pour chaque classe sur chaque instance de vol
CREATE TABLE vol_classe (
    id_vol_detail INT NOT NULL,
    id_classe INT NOT NULL,
    places_restantes INT CHECK (places_restantes >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_vol_detail, id_classe),
    CONSTRAINT fk_vol_classe_vol_detail FOREIGN KEY (id_vol_detail) 
        REFERENCES vol_detail(id_vol_detail) ON DELETE CASCADE,
    CONSTRAINT fk_vol_classe_classe FOREIGN KEY (id_classe) 
        REFERENCES classe_siege(id_classe) ON DELETE RESTRICT
);

CREATE INDEX idx_vol_classe_vol_detail ON vol_classe(id_vol_detail);
CREATE INDEX idx_vol_classe_classe ON vol_classe(id_classe);

-- =========================
-- TABLE REMISE_CLASSE_CATEGORIE
-- =========================
-- Pourcentage de tarif par classe et catégorie d'âge
-- Exemples: Adulte=100%, Enfant=75%, Bébé=10%
CREATE TABLE remise_classe_categorie (
    id_classe INT NOT NULL,
    id_categorie INT NOT NULL,
    pourcentage NUMERIC(5,2) NOT NULL DEFAULT 100.00 CHECK (pourcentage >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_classe, id_categorie),
    CONSTRAINT fk_remise_classe FOREIGN KEY (id_classe) 
        REFERENCES classe_siege(id_classe) ON DELETE CASCADE,
    CONSTRAINT fk_remise_categorie FOREIGN KEY (id_categorie) 
        REFERENCES categorie_age(id_categorie) ON DELETE CASCADE
);

CREATE INDEX idx_remise_classe ON remise_classe_categorie(id_classe);
CREATE INDEX idx_remise_categorie ON remise_classe_categorie(id_categorie);

-- =========================
-- TABLE PRIX_CLASSE_AGE
-- =========================
-- Prix spécifique pour chaque classe et catégorie d'âge par vol
CREATE TABLE prix_classe_age (
    id_vol_detail INT NOT NULL,
    id_classe INT NOT NULL,
    id_categorie INT NOT NULL,
    prix NUMERIC(10,2) NOT NULL CHECK (prix >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_vol_detail, id_classe, id_categorie),
    CONSTRAINT fk_prix_vol_detail FOREIGN KEY (id_vol_detail) 
        REFERENCES vol_detail(id_vol_detail) ON DELETE CASCADE,
    CONSTRAINT fk_prix_classe FOREIGN KEY (id_classe) 
        REFERENCES classe_siege(id_classe) ON DELETE RESTRICT,
    CONSTRAINT fk_prix_categorie FOREIGN KEY (id_categorie) 
        REFERENCES categorie_age(id_categorie) ON DELETE RESTRICT
);

CREATE INDEX idx_prix_vol_detail ON prix_classe_age(id_vol_detail);
CREATE INDEX idx_prix_classe ON prix_classe_age(id_classe);
CREATE INDEX idx_prix_categorie ON prix_classe_age(id_categorie);

-- =========================
-- TABLE RESERVATION
-- =========================
-- Réservations des passagers pour un vol
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_reservation VARCHAR(50) UNIQUE NOT NULL,
    statut VARCHAR(20) DEFAULT 'CONFIRMÉE',   -- CONFIRMÉE, ANNULÉE, COMPLÉTÉE
    id_utilisateur INT NOT NULL,
    id_vol_detail INT NOT NULL,
    id_classe INT NOT NULL,
    id_categorie INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_utilisateur FOREIGN KEY (id_utilisateur) 
        REFERENCES utilisateur(id_utilisateur) ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_vol_detail FOREIGN KEY (id_vol_detail) 
        REFERENCES vol_detail(id_vol_detail) ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_classe FOREIGN KEY (id_classe) 
        REFERENCES classe_siege(id_classe) ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_categorie FOREIGN KEY (id_categorie) 
        REFERENCES categorie_age(id_categorie) ON DELETE RESTRICT
);

CREATE INDEX idx_reservation_utilisateur ON reservation(id_utilisateur);
CREATE INDEX idx_reservation_vol_detail ON reservation(id_vol_detail);
CREATE INDEX idx_reservation_numero ON reservation(numero_reservation);
CREATE INDEX idx_reservation_statut ON reservation(statut);
CREATE INDEX idx_reservation_date ON reservation(date_reservation);

-- =========================
-- TABLE PAIEMENT
-- =========================
-- Enregistrement des paiements pour les réservations
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    montant NUMERIC(10,2) NOT NULL CHECK (montant >= 0),
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE',  -- EN_ATTENTE, CONFIRMÉ, REMBOURSÉ, ÉCHOUÉ
    id_reservation INT UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_paiement_reservation FOREIGN KEY (id_reservation) 
        REFERENCES reservation(id_reservation) ON DELETE CASCADE
);

CREATE INDEX idx_paiement_reservation ON paiement(id_reservation);
CREATE INDEX idx_paiement_statut ON paiement(statut);
CREATE INDEX idx_paiement_date ON paiement(date_paiement);

-- =========================
-- VUES UTILES
-- =========================

-- Vue: Récapitulatif des vol avec informations complètes
CREATE OR REPLACE VIEW v_vol_complet AS
SELECT 
    vd.id_vol_detail,
    v.numero_vol,
    c.code_iata AS compagnie_code,
    a.modele AS avion_modele,
    ad.code_iata AS aeroport_depart,
    ad.nom AS ville_depart,
    aa.code_iata AS aeroport_arrivee,
    aa.nom AS ville_arrivee,
    vd.date_heure_depart,
    vd.date_heure_arrivee,
    vd.statut,
    (vd.date_heure_arrivee - vd.date_heure_depart) AS duree_vol
FROM vol_detail vd
JOIN vol v ON vd.id_vol = v.id_vol
JOIN compagnie c ON v.id_compagnie = c.id_compagnie
JOIN avion a ON vd.id_avion = a.id_avion
JOIN aeroport ad ON v.id_aeroport_depart = ad.id_aeroport
JOIN aeroport aa ON v.id_aeroport_arrivee = aa.id_aeroport;

-- Vue: Disponibilités par vol et classe
CREATE OR REPLACE VIEW v_disponibilites AS
SELECT 
    vd.id_vol_detail,
    v.numero_vol,
    cs.code,
    cs.libelle,
    ac.capacite,
    COALESCE(vc.places_restantes, 0) AS places_restantes,
    (ac.capacite - COALESCE(vc.places_restantes, 0)) AS places_occupees
FROM vol_detail vd
JOIN vol v ON vd.id_vol = v.id_vol
JOIN avion a ON vd.id_avion = a.id_avion
JOIN avion_classe ac ON a.id_avion = ac.id_avion
JOIN classe_siege cs ON ac.id_classe = cs.id_classe
LEFT JOIN vol_classe vc ON vd.id_vol_detail = vc.id_vol_detail 
    AND vc.id_classe = cs.id_classe;

-- Vue: Tarifs actuels par vol, classe et catégorie
CREATE OR REPLACE VIEW v_tarifs_actuels AS
SELECT 
    pca.id_vol_detail,
    v.numero_vol,
    cs.libelle AS classe,
    ca.libelle AS categorie,
    pca.prix,
    rcc.pourcentage
FROM prix_classe_age pca
JOIN vol_detail vd ON pca.id_vol_detail = vd.id_vol_detail
JOIN vol v ON vd.id_vol = v.id_vol
JOIN classe_siege cs ON pca.id_classe = cs.id_classe
JOIN categorie_age ca ON pca.id_categorie = ca.id_categorie
LEFT JOIN remise_classe_categorie rcc 
    ON pca.id_classe = rcc.id_classe 
    AND pca.id_categorie = rcc.id_categorie;

-- =========================
-- CONTRAINTES DE SÉCURITÉ
-- =========================

-- Empêcher la modification de certains statuts critiques sans audit
CREATE OR REPLACE FUNCTION check_vol_detail_statut_change() 
RETURNS TRIGGER AS $$
BEGIN
    -- Vous pouvez ajouter une logique d'audit ici si nécessaire
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =========================
-- DONNÉES DE BASE (OPTIONNEL)
-- =========================
-- Insertion des classes de siège
INSERT INTO classe_siege (code, libelle) VALUES 
    ('ECONOMIQUE', 'Classe Économique'),
    ('BUSINESS', 'Classe Business'),
    ('PREMIERE', 'Première Classe')
ON CONFLICT (code) DO NOTHING;

-- Insertion des catégories d'âge
INSERT INTO categorie_age (libelle, age_min, age_max) VALUES 
    ('Adulte', 18, 120),
    ('Enfant', 2, 17),
    ('Bébé', 0, 1)
ON CONFLICT DO NOTHING;

-- Insertion des remises par défaut (100% = tarif normal)
INSERT INTO remise_classe_categorie (id_classe, id_categorie, pourcentage)
SELECT cs.id_classe, ca.id_categorie, 100.00
FROM classe_siege cs, categorie_age ca
WHERE NOT EXISTS (
    SELECT 1 FROM remise_classe_categorie 
    WHERE id_classe = cs.id_classe AND id_categorie = ca.id_categorie
)
ON CONFLICT (id_classe, id_categorie) DO NOTHING;

-- =========================
-- COMMENTAIRES DE DOCUMENTATION
-- =========================
COMMENT ON TABLE utilisateur IS 'Utilisateurs de l''application (administrateurs, agents, clients)';
COMMENT ON TABLE compagnie IS 'Compagnies aériennes';
COMMENT ON TABLE aeroport IS 'Aéroports de destination';
COMMENT ON TABLE classe_siege IS 'Types de classes disponibles (Économique, Business, Première)';
COMMENT ON TABLE categorie_age IS 'Catégories d''âge pour la tarification';
COMMENT ON TABLE avion IS 'Avions appartenant aux compagnies';
COMMENT ON TABLE avion_classe IS 'Capacité de chaque classe par avion';
COMMENT ON TABLE vol IS 'Définition des itinéraires de vol';
COMMENT ON TABLE vol_detail IS 'Instances spécifiques de vols (date, heure, avion)';
COMMENT ON TABLE vol_classe IS 'Places disponibles par classe pour chaque vol';
COMMENT ON TABLE remise_classe_categorie IS 'Pourcentage de tarif par classe et catégorie d''âge';
COMMENT ON TABLE prix_classe_age IS 'Prix spécifique par vol, classe et catégorie d''âge';
COMMENT ON TABLE reservation IS 'Réservations des passagers';
COMMENT ON TABLE paiement IS 'Paiements des réservations';
