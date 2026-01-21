DROP DATABASE IF EXISTS aero;
CREATE DATABASE aero;
\c aero;
-- =========================
-- TABLE UTILISATEUR
-- =========================
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL
);

-- =========================
-- TABLE COMPAGNIE
-- =========================
CREATE TABLE compagnie (
    id_compagnie SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    code_iata VARCHAR(5) UNIQUE NOT NULL,
    code_icao VARCHAR(5) UNIQUE NOT NULL,
    pays VARCHAR(100) NOT NULL
);

-- =========================
-- TABLE AEROPORT
-- =========================
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    code_iata VARCHAR(5) UNIQUE NOT NULL
);

-- =========================
-- TABLE CLASSE_SIEGE
-- =========================
CREATE TABLE classe_siege (
    id_classe SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    libelle VARCHAR(50) NOT NULL
);

-- =========================
-- TABLE CATEGORIE_AGE
-- =========================
CREATE TABLE categorie_age (
    id_categorie SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL,
    age_min INT NOT NULL,
    age_max INT NOT NULL,
    CONSTRAINT chk_age CHECK (age_min <= age_max)
);

-- =========================
-- TABLE AVION
-- =========================
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    modele VARCHAR(100) NOT NULL,
    statut VARCHAR(30) NOT NULL,
    id_compagnie INT NOT NULL,

    CONSTRAINT fk_avion_compagnie
        FOREIGN KEY (id_compagnie)
        REFERENCES compagnie(id_compagnie)
);

-- =========================
-- TABLE AVION_CLASSE
-- =========================
CREATE TABLE avion_classe (
    id_avion INT NOT NULL,
    id_classe INT NOT NULL,
    capacite INT NOT NULL,

    PRIMARY KEY (id_avion, id_classe),

    CONSTRAINT fk_avion_classe_avion
        FOREIGN KEY (id_avion)
        REFERENCES avion(id_avion),

    CONSTRAINT fk_avion_classe_classe
        FOREIGN KEY (id_classe)
        REFERENCES classe_siege(id_classe)
);

-- =========================
-- TABLE VOL
-- =========================
CREATE TABLE vol (
    id_vol SERIAL PRIMARY KEY,
    numero_vol VARCHAR(20) NOT NULL,

    id_compagnie INT NOT NULL,
    id_aeroport_depart INT NOT NULL,
    id_aeroport_arrivee INT NOT NULL,

    CONSTRAINT fk_vol_compagnie
        FOREIGN KEY (id_compagnie)
        REFERENCES compagnie(id_compagnie),

    CONSTRAINT fk_vol_aeroport_depart
        FOREIGN KEY (id_aeroport_depart)
        REFERENCES aeroport(id_aeroport),

    CONSTRAINT fk_vol_aeroport_arrivee
        FOREIGN KEY (id_aeroport_arrivee)
        REFERENCES aeroport(id_aeroport),

    CONSTRAINT chk_aeroport_different
        CHECK (id_aeroport_depart <> id_aeroport_arrivee)
);

-- =========================
-- TABLE VOL_DETAIL
-- =========================
CREATE TABLE vol_detail (
    id_vol_detail SERIAL PRIMARY KEY,
    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    statut VARCHAR(30) NOT NULL,

    id_vol INT NOT NULL,
    id_avion INT NOT NULL,

    CONSTRAINT fk_vol_detail_vol
        FOREIGN KEY (id_vol)
        REFERENCES vol(id_vol),

    CONSTRAINT fk_vol_detail_avion
        FOREIGN KEY (id_avion)
        REFERENCES avion(id_avion),

    CONSTRAINT chk_dates
        CHECK (date_heure_depart < date_heure_arrivee)
);

-- =========================
-- TABLE PRIX_CLASSE
-- =========================
CREATE TABLE prix_classe (
    id_vol_detail INT NOT NULL,
    id_classe INT NOT NULL,
    prix_base DECIMAL(10,2) NOT NULL,

    PRIMARY KEY (id_vol_detail, id_classe),

    CONSTRAINT fk_prix_vol_detail
        FOREIGN KEY (id_vol_detail)
        REFERENCES vol_detail(id_vol_detail),

    CONSTRAINT fk_prix_classe
        FOREIGN KEY (id_classe)
        REFERENCES classe_siege(id_classe)
);

-- =========================
-- TABLE REMISE_CLASSE_CATEGORIE
-- =========================
CREATE TABLE remise_classe_categorie (
    id_classe INT NOT NULL,
    id_categorie INT NOT NULL,
    pourcentage DECIMAL(5,2) NOT NULL,

    PRIMARY KEY (id_classe, id_categorie),

    CONSTRAINT fk_remise_classe
        FOREIGN KEY (id_classe)
        REFERENCES classe_siege(id_classe),

    CONSTRAINT fk_remise_categorie
        FOREIGN KEY (id_categorie)
        REFERENCES categorie_age(id_categorie),

    CONSTRAINT chk_pourcentage
        CHECK (pourcentage BETWEEN 0 AND 100)
);

-- =========================
-- TABLE RESERVATION
-- =========================
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    numero_reservation VARCHAR(30) UNIQUE NOT NULL,
    statut VARCHAR(30) NOT NULL,

    id_utilisateur INT NOT NULL,
    id_vol_detail INT NOT NULL,
    id_classe INT NOT NULL,
    id_categorie INT NOT NULL,

    CONSTRAINT fk_reservation_utilisateur
        FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateur(id_utilisateur),

    CONSTRAINT fk_reservation_vol_detail
        FOREIGN KEY (id_vol_detail)
        REFERENCES vol_detail(id_vol_detail),

    CONSTRAINT fk_reservation_classe
        FOREIGN KEY (id_classe)
        REFERENCES classe_siege(id_classe),

    CONSTRAINT fk_reservation_categorie
        FOREIGN KEY (id_categorie)
        REFERENCES categorie_age(id_categorie)
);

-- =========================
-- TABLE PAIEMENT
-- =========================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    montant DECIMAL(10,2) NOT NULL,
    statut VARCHAR(30) NOT NULL,

    id_reservation INT NOT NULL,

    CONSTRAINT fk_paiement_reservation
        FOREIGN KEY (id_reservation)
        REFERENCES reservation(id_reservation)
);
