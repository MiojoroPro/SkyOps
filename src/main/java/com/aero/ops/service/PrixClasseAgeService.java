package com.aero.ops.service;

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

    public PrixClasseAgeService(PrixClasseAgeRepository prixClasseAgeRepository) {
        this.prixClasseAgeRepository = prixClasseAgeRepository;
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
     * Retourne le prix pour une combinaison volDetail/classe/categorie.
     * Retourne BigDecimal.ZERO si non trouvé.
     */
    public BigDecimal getMontant(Long idVolDetail, Long idClasse, Long idCategorie) {
        return getPrix(idVolDetail, idClasse, idCategorie)
                .map(PrixClasseAge::getPrix)
                .orElse(BigDecimal.ZERO);
    }

    public PrixClasseAge save(PrixClasseAge prixClasseAge) {
        return prixClasseAgeRepository.save(prixClasseAge);
    }

    @Transactional
    public void deleteByVolDetail(Long idVolDetail) {
        prixClasseAgeRepository.deleteByVolDetail_IdVolDetail(idVolDetail);
    }
}
