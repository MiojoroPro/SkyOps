package com.aero.ops.service;

import com.aero.ops.model.RemiseClasseCategorie;
import com.aero.ops.repository.RemiseClasseCategorieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class RemiseService {

    private final RemiseClasseCategorieRepository remiseRepository;

    public RemiseService(RemiseClasseCategorieRepository remiseRepository) {
        this.remiseRepository = remiseRepository;
    }

    public List<RemiseClasseCategorie> getByClasse(Long idClasse) {
        return remiseRepository.findByClasseSiege_IdClasse(idClasse);
    }

    public List<RemiseClasseCategorie> getByCategorie(Long idCategorie) {
        return remiseRepository.findByCategorieAge_IdCategorie(idCategorie);
    }

    public Optional<RemiseClasseCategorie> getByClasseAndCategorie(Long idClasse, Long idCategorie) {
        return remiseRepository.findByClasseAndCategorie(idClasse, idCategorie);
    }

    /**
     * Calcule le prix final avec la remise appliquée
     */
    public BigDecimal calculerPrixAvecRemise(BigDecimal prixBase, Long idClasse, Long idCategorie) {
        if (prixBase == null || prixBase.compareTo(BigDecimal.ZERO) <= 0) {
            return prixBase;
        }
        
        Optional<RemiseClasseCategorie> remise = getByClasseAndCategorie(idClasse, idCategorie);
        if (remise.isPresent() && remise.get().getPourcentage() != null) {
            BigDecimal pourcentageReduction = remise.get().getPourcentage()
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            return prixBase.multiply(BigDecimal.ONE.subtract(pourcentageReduction))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        return prixBase;
    }

    public RemiseClasseCategorie save(RemiseClasseCategorie remise) {
        return remiseRepository.save(remise);
    }

    @Transactional
    public void delete(RemiseClasseCategorie remise) {
        remiseRepository.delete(remise);
    }

    public List<RemiseClasseCategorie> getAll() {
        return remiseRepository.findAllOrderByClasseAndCategorie();
    }
}
