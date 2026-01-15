DROP DATABASE IF EXISTS aero;
CREATE DATABASE aero;
\c aero;

-- =========================
-- TABLE : UTILISATEUR
-- =========================
CREATE TABLE utilisateur (
    id_utilisateur SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- =========================
-- TABLE : COMPAGNIE
-- =========================
CREATE TABLE compagnie (
    id_compagnie SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    code_iata VARCHAR(5) UNIQUE NOT NULL,
    code_icao VARCHAR(5) UNIQUE NOT NULL,
    pays VARCHAR(100) NOT NULL
);

-- =========================
-- TABLE : AEROPORT
-- =========================
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ville VARCHAR(100) NOT NULL,
    pays VARCHAR(100) NOT NULL,
    code_iata VARCHAR(5) UNIQUE NOT NULL
);

-- =========================
-- TABLE : AVION
-- Un avion possède des places économiques et première classe
-- =========================
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    modele VARCHAR(100) NOT NULL,

    capacite_economique INT NOT NULL CHECK (capacite_economique > 0),
    capacite_premiere INT NOT NULL CHECK (capacite_premiere >= 0),

    capacite_totale INT GENERATED ALWAYS AS
        (capacite_economique + capacite_premiere) STORED,

    statut VARCHAR(20) NOT NULL,
    id_compagnie INT NOT NULL,

    CONSTRAINT fk_avion_compagnie
        FOREIGN KEY (id_compagnie)
        REFERENCES compagnie(id_compagnie)
);

-- =========================
-- TABLE : VOL (ligne aérienne)
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
-- TABLE : VOL_PLANIFIE (VOL_DETAIL)
-- Prix et places définis ici par classe
-- =========================
CREATE TABLE vol_detail (
    id_vol_detail SERIAL PRIMARY KEY,

    date_heure_depart TIMESTAMP NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,

    prix_economique NUMERIC(10,2) NOT NULL CHECK (prix_economique >= 0),
    prix_premiere NUMERIC(10,2) NOT NULL CHECK (prix_premiere >= 0),

    places_eco_restantes INT NOT NULL CHECK (places_eco_restantes >= 0),
    places_premiere_restantes INT NOT NULL CHECK (places_premiere_restantes >= 0),

    statut VARCHAR(20) NOT NULL,

    id_vol INT NOT NULL,
    id_avion INT NOT NULL,

    CONSTRAINT fk_vol_detail_vol
        FOREIGN KEY (id_vol)
        REFERENCES vol(id_vol),

    CONSTRAINT fk_vol_detail_avion
        FOREIGN KEY (id_avion)
        REFERENCES avion(id_avion)
);

-- =========================
-- TABLE : RESERVATION
-- La classe est choisie ici
-- =========================
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    date_reservation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    numero_reservation VARCHAR(50) UNIQUE NOT NULL,

    classe VARCHAR(20) NOT NULL
        CHECK (classe IN ('ECONOMIQUE', 'PREMIERE')),

    statut VARCHAR(20) NOT NULL,

    id_utilisateur INT NOT NULL,
    id_vol_detail INT NOT NULL,

    CONSTRAINT fk_reservation_utilisateur
        FOREIGN KEY (id_utilisateur)
        REFERENCES utilisateur(id_utilisateur),

    CONSTRAINT fk_reservation_vol_detail
        FOREIGN KEY (id_vol_detail)
        REFERENCES vol_detail(id_vol_detail)
);

-- =========================
-- TABLE : PAIEMENT
-- =========================
CREATE TABLE paiement (
    id_paiement SERIAL PRIMARY KEY,
    date_paiement TIMESTAMP,
    montant NUMERIC(10,2) NOT NULL CHECK (montant >= 0),
    statut VARCHAR(20) NOT NULL,

    id_reservation INT UNIQUE NOT NULL,

    CONSTRAINT fk_paiement_reservation
        FOREIGN KEY (id_reservation)
        REFERENCES reservation(id_reservation)
);
