-- Script pour corriger les paiements publicitaires orphelins
-- Exécuter sur la base 'aero'

\c aero;

-- Option 1: Supprimer les paiements qui référencent des diffusions inexistantes
DELETE FROM paiement_publicitaire 
WHERE id_diffusion NOT IN (SELECT id_diffusion FROM diffusion_publicitaire);

-- Vérification: afficher les paiements restants
SELECT pp.*, dp.id_publicite 
FROM paiement_publicitaire pp 
LEFT JOIN diffusion_publicitaire dp ON pp.id_diffusion = dp.id_diffusion;
