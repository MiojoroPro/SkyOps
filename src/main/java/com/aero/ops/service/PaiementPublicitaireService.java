package com.aero.ops.service;

import com.aero.ops.model.PaiementPublicitaire;
import com.aero.ops.repository.PaiementPublicitaireRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaiementPublicitaireService {

    private final PaiementPublicitaireRepository paiementPublicitaireRepository;

    public PaiementPublicitaireService(PaiementPublicitaireRepository paiementPublicitaireRepository) {
        this.paiementPublicitaireRepository = paiementPublicitaireRepository;
    }

    public List<PaiementPublicitaire> getAll() {
        return paiementPublicitaireRepository.findAll();
    }

    public PaiementPublicitaire getById(Long id) {
        return paiementPublicitaireRepository.findById(id).orElse(null);
    }

    public List<PaiementPublicitaire> getByDiffusion(Long idDiffusion) {
        return paiementPublicitaireRepository.findByDiffusionIdDiffusion(idDiffusion);
    }

    public BigDecimal getTotalPayeByDiffusion(Long idDiffusion) {
        return paiementPublicitaireRepository.getTotalPayeByDiffusion(idDiffusion);
    }

    public PaiementPublicitaire create(PaiementPublicitaire paiement) {
        return paiementPublicitaireRepository.save(paiement);
    }

    public PaiementPublicitaire update(PaiementPublicitaire paiement) {
        return paiementPublicitaireRepository.save(paiement);
    }

    public void delete(Long id) {
        paiementPublicitaireRepository.deleteById(id);
    }
}
