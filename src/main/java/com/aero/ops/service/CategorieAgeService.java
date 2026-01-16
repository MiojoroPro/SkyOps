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
