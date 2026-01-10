package com.aero.ops.service;

import com.aero.ops.model.Compagnie;
import com.aero.ops.repository.CompagnieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompagnieService {

    private final CompagnieRepository compagnieRepository;

    public CompagnieService(CompagnieRepository compagnieRepository) {
        this.compagnieRepository = compagnieRepository;
    }

    public List<Compagnie> getAll() {
        return compagnieRepository.findAll();
    }

    public Compagnie getById(Long id) {
        return compagnieRepository.findById(id).orElse(null);
    }

    public Compagnie create(Compagnie compagnie) {
        return compagnieRepository.save(compagnie);
    }

    public Compagnie update(Compagnie compagnie) {
        return compagnieRepository.save(compagnie);
    }

    public void delete(Long id) {
        compagnieRepository.deleteById(id);
    }
}
