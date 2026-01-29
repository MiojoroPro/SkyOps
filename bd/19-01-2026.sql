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


CREATE TABLE societe_annonceur (
    id_societe SERIAL PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    telephone VARCHAR(30)
);


CREATE TABLE publicite (
    id_publicite SERIAL PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    duree_seconde INT NOT NULL,
    description TEXT,

    id_societe INT NOT NULL,

    CONSTRAINT fk_publicite_societe
        FOREIGN KEY (id_societe)
        REFERENCES societe_annonceur(id_societe)
);

CREATE TABLE tarif_publicitaire (
    id_tarif SERIAL PRIMARY KEY,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE
);

CREATE TABLE diffusion_publicitaire (
    id_diffusion SERIAL PRIMARY KEY,
    mois INT NOT NULL,
    annee INT NOT NULL,
    nombre_diffusions INT NOT NULL CHECK (nombre_diffusions > 0),

    id_publicite INT NOT NULL,
    id_tarif INT NOT NULL,
    id_vol_detail INT NOT NULL,

    CONSTRAINT fk_diffusion_publicite
        FOREIGN KEY (id_publicite)
        REFERENCES publicite(id_publicite),

    CONSTRAINT fk_diffusion_tarif
        FOREIGN KEY (id_tarif)
        REFERENCES tarif_publicitaire(id_tarif),

    CONSTRAINT fk_diffusion_vol_detail
        FOREIGN KEY (id_vol_detail)
        REFERENCES vol_detail(id_vol_detail),

    CONSTRAINT chk_mois CHECK (mois BETWEEN 1 AND 12),

    CONSTRAINT uq_diffusion_unique
        UNIQUE (id_publicite, id_vol_detail, mois, annee)
);


-- =========================
-- TABLE PAIEMENT_PUBLICITAIRE
-- =========================
CREATE TABLE paiement_publicitaire (
    id_paiement_pub SERIAL PRIMARY KEY,
    montant DECIMAL(10,2) NOT NULL,
    date_paiement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reference VARCHAR(50),
    mode_paiement VARCHAR(30),

    id_diffusion INT NOT NULL,

    CONSTRAINT fk_paiement_pub_diffusion
        FOREIGN KEY (id_diffusion)
        REFERENCES diffusion_publicitaire(id_diffusion)
);

-- =========================
-- TABLE PRODUIT_EXTRA
-- =========================
-- Produits vendus par les compagnies aériennes (eau, snacks, etc.)
CREATE TABLE produit_extra (
    id_produit SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    prix_unitaire DECIMAL(10,2) NOT NULL CHECK (prix_unitaire > 0),
    
    id_compagnie INT NOT NULL,
    
    CONSTRAINT fk_produit_compagnie
        FOREIGN KEY (id_compagnie)
        REFERENCES compagnie(id_compagnie)
);

-- =========================
-- TABLE VENTE_PRODUIT
-- =========================
-- Ventes de produits extra pendant un vol
CREATE TABLE vente_produit (
    id_vente SERIAL PRIMARY KEY,
    quantite INT NOT NULL CHECK (quantite > 0),
    prix_unitaire_vente DECIMAL(10,2) NOT NULL,
    date_vente TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    id_produit INT NOT NULL,
    id_vol_detail INT NOT NULL,
    
    CONSTRAINT fk_vente_produit
        FOREIGN KEY (id_produit)
        REFERENCES produit_extra(id_produit),
    
    CONSTRAINT fk_vente_vol_detail
        FOREIGN KEY (id_vol_detail)
        REFERENCES vol_detail(id_vol_detail)
);


