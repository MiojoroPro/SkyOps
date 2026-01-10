package com.aero.ops.service;

import com.aero.ops.model.Avion;
import com.aero.ops.repository.AvionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvionService {

    private final AvionRepository avionRepository;

    public AvionService(AvionRepository avionRepository) {
        this.avionRepository = avionRepository;
    }

    public List<Avion> getAll() {
        return avionRepository.findAll();
    }

    public Avion getById(Long id) {
        return avionRepository.findById(id).orElse(null);
    }

    public Avion create(Avion avion) {
        return avionRepository.save(avion);
    }

    public Avion update(Avion avion) {
        return avionRepository.save(avion);
    }

    public void delete(Long id) {
        avionRepository.deleteById(id);
    }
}
