package com.aero.ops.service;

import com.aero.ops.model.AvionClasse;
import com.aero.ops.model.AvionClasseId;
import com.aero.ops.repository.AvionClasseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvionClasseService {

    private final AvionClasseRepository avionClasseRepository;

    public AvionClasseService(AvionClasseRepository avionClasseRepository) {
        this.avionClasseRepository = avionClasseRepository;
    }

    public List<AvionClasse> getByAvion(Long idAvion) {
        return avionClasseRepository.findByAvion_IdAvion(idAvion);
    }

    public AvionClasse save(AvionClasse avionClasse) {
        return avionClasseRepository.save(avionClasse);
    }

    @Transactional
    public void deleteByAvion(Long idAvion) {
        avionClasseRepository.deleteByAvion_IdAvion(idAvion);
    }

    /**
     * Retourne la capacité d'une classe pour un avion donné.
     */
    public Integer getCapacite(Long idAvion, Long idClasse) {
        return avionClasseRepository.findById(new AvionClasseId(idAvion, idClasse))
                .map(AvionClasse::getCapacite)
                .orElse(0);
    }
}
