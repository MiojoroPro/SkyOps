package com.aero.ops.service;

import com.aero.ops.model.CategorieAge;
import com.aero.ops.repository.CategorieAgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieAgeService {

    private final CategorieAgeRepository categorieAgeRepository;

    public CategorieAgeService(CategorieAgeRepository categorieAgeRepository) {
        this.categorieAgeRepository = categorieAgeRepository;
    }

    public List<CategorieAge> getAll() {
        return categorieAgeRepository.findAll();
    }

    public CategorieAge getById(Long id) {
        return categorieAgeRepository.findById(id).orElse(null);
    }

    public Optional<CategorieAge> getByAge(int age) {
        return categorieAgeRepository.findByAge(age);
    }

    /**
     * Retourne la catégorie "Adulte" (basée sur le libellé ou l'âge min >= 18).
     * C'est la catégorie de référence pour le tarif de base.
     */
    public CategorieAge getCategorieAdulte() {
        return categorieAgeRepository.findAll().stream()
                .filter(c -> c.getLibelle() != null 
                        && c.getLibelle().toLowerCase().contains("adulte"))
                .findFirst()
                .orElseGet(() -> categorieAgeRepository.findAll().stream()
                        .filter(c -> c.getAgeMin() != null && c.getAgeMin() >= 18)
                        .findFirst()
                        .orElse(null));
    }

    public CategorieAge create(CategorieAge categorieAge) {
        return categorieAgeRepository.save(categorieAge);
    }

    public CategorieAge update(CategorieAge categorieAge) {
        return categorieAgeRepository.save(categorieAge);
    }

    public void delete(Long id) {
        categorieAgeRepository.deleteById(id);
    }
}
