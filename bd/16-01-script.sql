-- =========================
-- DROP DATABASE (optionnel)
-- =========================
-- DROP DATABASE IF EXISTS aero;
-- CREATE DATABASE aero;
-- \c aero;

-- =========================
-- TABLE UTILISATEUR
-- =========================
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    mot_de_passe VARCHAR(255),
    role VARCHAR(20)
);

-- =========================
-- TABLE COMPAGNIE
-- =========================
CREATE TABLE compagnie (
    id_compagnie SERIAL PRIMARY KEY,
    nom VARCHAR(150),
    code_iata VARCHAR(5) UNIQUE,
    code_icao VARCHAR(5) UNIQUE,
    pays VARCHAR(100)
);

-- =========================
-- TABLE AEROPORT
-- =========================
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(150),
    ville VARCHAR(100),
    pays VARCHAR(100),
    code_iata VARCHAR(5) UNIQUE
);

-- =========================
-- TABLE AVION
-- =========================
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    modele VARCHAR(100),
    statut VARCHAR(20),
    id_compagnie INT,
    CONSTRAINT fk_avion_compagnie FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie)
);

-- =========================
-- TABLE CLASSE DE SIEGE
-- =========================
CREATE TABLE classe_siege (
    id_classe SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE, -- ECONOMIQUE, PREMIERE, BUSINESS...
    libelle VARCHAR(50)
);

-- =========================
-- TABLE AVION_CLASSE (capacité par classe)
-- =========================
CREATE TABLE avion_classe (
    id_avion INT,
    id_classe INT,
    capacite INT CHECK (capacite > 0),
    PRIMARY KEY (id_avion, id_classe),
    CONSTRAINT fk_avion_classe_avion FOREIGN KEY (id_avion) REFERENCES avion(id_avion),
    CONSTRAINT fk_avion_classe_classe FOREIGN KEY (id_classe) REFERENCES classe_siege(id_classe)
);

-- =========================
-- TABLE VOL
-- =========================
CREATE TABLE vol (
    id_vol SERIAL PRIMARY KEY,
    numero_vol VARCHAR(20),
    id_compagnie INT,
    id_aeroport_depart INT,
    id_aeroport_arrivee INT,
    CONSTRAINT fk_vol_compagnie FOREIGN KEY (id_compagnie) REFERENCES compagnie(id_compagnie),
    CONSTRAINT fk_vol_aeroport_depart FOREIGN KEY (id_aeroport_depart) REFERENCES aeroport(id_aeroport),
    CONSTRAINT fk_vol_aeroport_arrivee FOREIGN KEY (id_aeroport_arrivee) REFERENCES aeroport(id_aeroport),
    CONSTRAINT chk_vol_diff_aeroport CHECK (id_aeroport_depart <> id_aeroport_arrivee)
);

-- =========================
-- TABLE VOL_DETAIL (planification)
-- =========================
CREATE TABLE vol_detail (
    id_vol_detail SERIAL PRIMARY KEY,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    statut VARCHAR(20),
    id_vol INT,
    id_avion INT,
    CONSTRAINT fk_vol_detail_vol FOREIGN KEY (id_vol) REFERENCES vol(id_vol),
    CONSTRAINT fk_vol_detail_avion FOREIGN KEY (id_avion) REFERENCES avion(id_avion)
);

-- =========================
-- TABLE VOL_CLASSE (places restantes par classe)
-- =========================
CREATE TABLE vol_classe (
    id_vol_detail INT,
    id_classe INT,
    places_restantes INT CHECK (places_restantes >= 0),
    PRIMARY KEY (id_vol_detail, id_classe),
    CONSTRAINT fk_vol_classe_vol_detail FOREIGN KEY (id_vol_detail) REFERENCES vol_detail(id_vol_detail),
    CONSTRAINT fk_vol_classe_classe FOREIGN KEY (id_classe) REFERENCES classe_siege(id_classe)
);

-- =========================
-- TABLE CATEGORIE_AGE
-- =========================
CREATE TABLE categorie_age (
    id_categorie SERIAL PRIMARY KEY,
    libelle VARCHAR(50),
    age_min INT NOT NULL,
    age_max INT NOT NULL
);

-- =========================
-- TABLE REMISE_CLASSE_CATEGORIE (pourcentage du tarif adulte par classe et categorie)
-- =========================
CREATE TABLE remise_classe_categorie (
    id_classe INT,
    id_categorie INT,
    pourcentage NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    PRIMARY KEY (id_classe, id_categorie),
    CONSTRAINT fk_remise_classe FOREIGN KEY (id_classe) REFERENCES classe_siege(id_classe),
    CONSTRAINT fk_remise_categorie FOREIGN KEY (id_categorie) REFERENCES categorie_age(id_categorie)
);

-- =========================
-- TABLE PRIX_CLASSE_AGE
-- =========================
CREATE TABLE prix_classe_age (
    id_vol_detail INT,
    id_classe INT,
    id_categorie INT,
    prix NUMERIC(10,2) CHECK (prix >= 0),
    PRIMARY KEY (id_vol_detail, id_classe, id_categorie),
    CONSTRAINT fk_prix_vol_detail FOREIGN KEY (id_vol_detail) REFERENCES vol_detail(id_vol_detail),
    CONSTRAINT fk_prix_classe FOREIGN KEY (id_classe) REFERENCES classe_siege(id_classe),
    CONSTRAINT fk_prix_categorie FOREIGN KEY (id_categorie) REFERENCES categorie_age(id_categorie)
);

-- =========================
-- TABLE RESERVATION
-- =========================
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    numero_reservation VARCHAR(50) UNIQUE,
    statut VARCHAR(20),
    id_utilisateur INT,
    id_vol_detail INT,
    id_classe INT,
    id_categorie INT,
    CONSTRAINT fk_reservation_utilisateur FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur),
    CONSTRAINT fk_reservation_vol_detail FOREIGN KEY (id_vol_detail) REFERENCES vol_detail(id_vol_detail),
    CONSTRAINT fk_reservation_classe FOREIGN KEY (id_classe) REFERENCES classe_siege(id_classe),
    CONSTRAINT fk_reservation_categorie FOREIGN KEY (id_categorie) REFERENCES categorie_age(id_categorie)
);

-- =========================
-- TABLE PAIEMENT
-- =========================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    date_paiement TIMESTAMP,
    montant NUMERIC(10,2) CHECK (montant >= 0),
    statut VARCHAR(20),
    id_reservation INT UNIQUE,
    CONSTRAINT fk_paiement_reservation FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation)
);
