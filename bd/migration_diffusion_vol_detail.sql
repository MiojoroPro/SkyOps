-- Migration: Changer la relation de diffusion_publicitaire de avion vers vol_detail
-- Date: 23/01/2026

-- Étape 1: Supprimer la contrainte de clé étrangère existante sur id_avion
ALTER TABLE diffusion_publicitaire DROP CONSTRAINT IF EXISTS fk_diffusion_avion;

-- Étape 2: Supprimer la contrainte d'unicité existante
ALTER TABLE diffusion_publicitaire DROP CONSTRAINT IF EXISTS uq_diffusion_unique;

-- Étape 3: Supprimer la colonne id_avion
ALTER TABLE diffusion_publicitaire DROP COLUMN IF EXISTS id_avion;

-- Étape 4: Ajouter la nouvelle colonne id_vol_detail
ALTER TABLE diffusion_publicitaire ADD COLUMN id_vol_detail INT;

-- Étape 5: Ajouter la contrainte de clé étrangère vers vol_detail
ALTER TABLE diffusion_publicitaire 
ADD CONSTRAINT fk_diffusion_vol_detail 
FOREIGN KEY (id_vol_detail) REFERENCES vol_detail(id_vol_detail);

-- Étape 6: Ajouter la nouvelle contrainte d'unicité
ALTER TABLE diffusion_publicitaire 
ADD CONSTRAINT uq_diffusion_unique 
UNIQUE (id_publicite, id_vol_detail, mois, annee);

-- Étape 7: Rendre la colonne NOT NULL (après avoir ajouté les données si nécessaire)
-- Note: Exécutez cette ligne seulement après avoir mis à jour les données existantes
-- ALTER TABLE diffusion_publicitaire ALTER COLUMN id_vol_detail SET NOT NULL;
