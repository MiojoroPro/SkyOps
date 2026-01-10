package com.aero.ops.service;

import com.aero.ops.model.Aeroport;
import com.aero.ops.repository.AeroportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AeroportService {

    private final AeroportRepository aeroportRepository;

    public AeroportService(AeroportRepository aeroportRepository) {
        this.aeroportRepository = aeroportRepository;
    }

    public List<Aeroport> getAll() {
        return aeroportRepository.findAll();
    }

    public Aeroport getById(Long id) {
        return aeroportRepository.findById(id).orElse(null);
    }

    public Aeroport create(Aeroport aeroport) {
        return aeroportRepository.save(aeroport);
    }

    public Aeroport update(Aeroport aeroport) {
        return aeroportRepository.save(aeroport);
    }

    public void delete(Long id) {
        aeroportRepository.deleteById(id);
    }
}
