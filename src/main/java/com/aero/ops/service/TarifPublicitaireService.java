package com.aero.ops.service;

import com.aero.ops.model.TarifPublicitaire;
import com.aero.ops.repository.TarifPublicitaireRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TarifPublicitaireService {

    private final TarifPublicitaireRepository tarifPublicitaireRepository;

    public TarifPublicitaireService(TarifPublicitaireRepository tarifPublicitaireRepository) {
        this.tarifPublicitaireRepository = tarifPublicitaireRepository;
    }

    public List<TarifPublicitaire> getAll() {
        return tarifPublicitaireRepository.findAll();
    }

    public TarifPublicitaire getById(Long id) {
        return tarifPublicitaireRepository.findById(id).orElse(null);
    }

    public Optional<TarifPublicitaire> getTarifApplicable(LocalDate date) {
        return tarifPublicitaireRepository.findTarifApplicable(date);
    }

    public TarifPublicitaire create(TarifPublicitaire tarif) {
        return tarifPublicitaireRepository.save(tarif);
    }

    public TarifPublicitaire update(TarifPublicitaire tarif) {
        return tarifPublicitaireRepository.save(tarif);
    }

    public void delete(Long id) {
        tarifPublicitaireRepository.deleteById(id);
    }
}
