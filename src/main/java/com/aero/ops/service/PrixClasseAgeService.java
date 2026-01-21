package com.aero.ops.service;

import com.aero.ops.model.CategorieAge;
import com.aero.ops.model.PrixClasseAge;
import com.aero.ops.repository.PrixClasseAgeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PrixClasseAgeService {

    private final PrixClasseAgeRepository prixClasseAgeRepository;
    private final CategorieAgeService categorieAgeService;
    private final RemiseClasseCategorieService remiseService;

    public PrixClasseAgeService(PrixClasseAgeRepository prixClasseAgeRepository,
                                CategorieAgeService categorieAgeService,
                                RemiseClasseCategorieService remiseService) {
        this.prixClasseAgeRepository = prixClasseAgeRepository;
        this.categorieAgeService = categorieAgeService;
        this.remiseService = remiseService;
    }

    public List<PrixClasseAge> getByVolDetail(Long idVolDetail) {
        return prixClasseAgeRepository.findByVolDetail_IdVolDetail(idVolDetail);
    }

    public List<PrixClasseAge> getByVolDetailAndClasse(Long idVolDetail, Long idClasse) {
        return prixClasseAgeRepository.findByVolDetail_IdVolDetailAndClasseSiege_IdClasse(idVolDetail, idClasse);
    }

    public Optional<PrixClasseAge> getPrix(Long idVolDetail, Long idClasse, Long idCategorie) {
        return prixClasseAgeRepository.findByVolDetail_IdVolDetailAndClasseSiege_IdClasseAndCategorieAge_IdCategorie(
                idVolDetail, idClasse, idCategorie);
    }

    /**
     * Retourne le prix adulte (base) pour une classe donnée.
     * Cherche la catégorie Adulte (pourcentage = 100% pour cette classe).
     */
    public BigDecimal getPrixAdulte(Long idVolDetail, Long idClasse) {
        CategorieAge categorieAdulte = categorieAgeService.getCategorieAdulte();
        if (categorieAdulte == null) {
            // Fallback: prendre le premier prix trouvé
            List<PrixClasseAge> prix = getByVolDetailAndClasse(idVolDetail, idClasse);
            return prix.isEmpty() ? BigDecimal.ZERO : prix.get(0).getPrix();
        }
        // Récupérer le prix stocké pour l'adulte
        Optional<PrixClasseAge> prixOpt = getPrix(idVolDetail, idClasse, categorieAdulte.getIdCategorie());
        return prixOpt.map(PrixClasseAge::getPrix).orElse(BigDecimal.ZERO);
    }

    /**
     * Retourne le prix calculé pour une combinaison volDetail/classe/categorie.
     * Le prix est calculé: prix_adulte * pourcentage_classe_categorie / 100
     */
    public BigDecimal getMontant(Long idVolDetail, Long idClasse, Long idCategorie) {
        // Récupérer le prix stocké
        Optional<PrixClasseAge> prixOpt = getPrix(idVolDetail, idClasse, idCategorie);
        if (prixOpt.isPresent() && prixOpt.get().getPrix() != null) {
            return prixOpt.get().getPrix();
        }
        
        // Sinon, calculer à partir du prix adulte et du pourcentage classe/catégorie
        BigDecimal prixAdulte = getPrixAdulte(idVolDetail, idClasse);
        return remiseService.calculerPrixAvecRemise(prixAdulte, idClasse, idCategorie);
    }

    public PrixClasseAge save(PrixClasseAge prixClasseAge) {
        return prixClasseAgeRepository.save(prixClasseAge);
    }

    @Transactional
    public void deleteByVolDetail(Long idVolDetail) {
        prixClasseAgeRepository.deleteByVolDetail_IdVolDetail(idVolDetail);
    }
}
