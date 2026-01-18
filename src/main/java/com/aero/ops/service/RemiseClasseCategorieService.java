package com.aero.ops.service;

import com.aero.ops.model.CategorieAge;
import com.aero.ops.model.ClasseSiege;
import com.aero.ops.model.RemiseClasseCategorie;
import com.aero.ops.model.RemiseClasseCategorieId;
import com.aero.ops.repository.CategorieAgeRepository;
import com.aero.ops.repository.ClasseSiegeRepository;
import com.aero.ops.repository.RemiseClasseCategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class RemiseClasseCategorieService {

    @Autowired
    private RemiseClasseCategorieRepository remiseRepository;

    @Autowired
    private ClasseSiegeRepository classeRepository;

    @Autowired
    private CategorieAgeRepository categorieRepository;

    public List<RemiseClasseCategorie> findAll() {
        return remiseRepository.findAllOrderByClasseAndCategorie();
    }

    public List<RemiseClasseCategorie> findByClasse(Long idClasse) {
        return remiseRepository.findByClasseSiege_IdClasse(idClasse);
    }

    public List<RemiseClasseCategorie> findByCategorie(Long idCategorie) {
        return remiseRepository.findByCategorieAge_IdCategorie(idCategorie);
    }

    public Optional<RemiseClasseCategorie> findByClasseAndCategorie(Long idClasse, Long idCategorie) {
        return remiseRepository.findByClasseAndCategorie(idClasse, idCategorie);
    }

    public RemiseClasseCategorie save(Long idClasse, Long idCategorie, BigDecimal pourcentage) {
        ClasseSiege classe = classeRepository.findById(idClasse)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
        CategorieAge categorie = categorieRepository.findById(idCategorie)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        RemiseClasseCategorie remise = new RemiseClasseCategorie();
        remise.setId(new RemiseClasseCategorieId(idClasse, idCategorie));
        remise.setClasseSiege(classe);
        remise.setCategorieAge(categorie);
        remise.setPourcentage(pourcentage);

        return remiseRepository.save(remise);
    }

    public void delete(Long idClasse, Long idCategorie) {
        remiseRepository.deleteById(new RemiseClasseCategorieId(idClasse, idCategorie));
    }

    /**
     * Obtient le pourcentage de remise pour une classe et catégorie
     * Retourne 100 par défaut si non défini
     */
    public BigDecimal getPourcentage(Long idClasse, Long idCategorie) {
        return findByClasseAndCategorie(idClasse, idCategorie)
                .map(RemiseClasseCategorie::getPourcentage)
                .orElse(BigDecimal.valueOf(100));
    }

    /**
     * Calcule le prix pour une classe et catégorie à partir du prix adulte
     */
    public BigDecimal calculerPrix(BigDecimal prixAdulte, Long idClasse, Long idCategorie) {
        BigDecimal pourcentage = getPourcentage(idClasse, idCategorie);
        return prixAdulte.multiply(pourcentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /**
     * Initialise les remises par défaut pour toutes les combinaisons classe/catégorie
     */
    public void initRemisesDefaut() {
        List<ClasseSiege> classes = classeRepository.findAll();
        List<CategorieAge> categories = categorieRepository.findAll();

        for (ClasseSiege classe : classes) {
            for (CategorieAge categorie : categories) {
                if (!findByClasseAndCategorie(classe.getIdClasse(), categorie.getIdCategorie()).isPresent()) {
                    // Par défaut: 100% pour tous
                    save(classe.getIdClasse(), categorie.getIdCategorie(), BigDecimal.valueOf(100));
                }
            }
        }
    }
}
