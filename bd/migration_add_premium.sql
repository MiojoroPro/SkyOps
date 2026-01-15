-- Migration: add PREMIUM fields to Avion, Vol, VolDetail
ALTER TABLE avion ADD COLUMN IF NOT EXISTS capacite_premium INT DEFAULT 0;
ALTER TABLE vol ADD COLUMN IF NOT EXISTS prix_premium NUMERIC(10,2);
ALTER TABLE vol_detail ADD COLUMN IF NOT EXISTS prix_premium NUMERIC(10,2);
ALTER TABLE vol_detail ADD COLUMN IF NOT EXISTS places_premium_restantes INT DEFAULT 0;

-- Optional: populate premium places with avion capacity for existing vol_detail rows
UPDATE vol_detail vd
SET places_premium_restantes = COALESCE(a.capacite_premium, 0)
FROM avion a
WHERE vd.id_avion = a.id_avion AND (vd.places_premium_restantes IS NULL OR vd.places_premium_restantes = 0);

-- Note: review and adjust constraints/indexes as needed for your environment
