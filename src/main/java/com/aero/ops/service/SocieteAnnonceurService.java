package com.aero.ops.service;

import com.aero.ops.model.SocieteAnnonceur;
import com.aero.ops.repository.SocieteAnnonceurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SocieteAnnonceurService {

    private final SocieteAnnonceurRepository societeAnnonceurRepository;

    public SocieteAnnonceurService(SocieteAnnonceurRepository societeAnnonceurRepository) {
        this.societeAnnonceurRepository = societeAnnonceurRepository;
    }

    public List<SocieteAnnonceur> getAll() {
        return societeAnnonceurRepository.findAll();
    }

    public SocieteAnnonceur getById(Long id) {
        return societeAnnonceurRepository.findById(id).orElse(null);
    }

    public SocieteAnnonceur create(SocieteAnnonceur societe) {
        return societeAnnonceurRepository.save(societe);
    }

    public SocieteAnnonceur update(SocieteAnnonceur societe) {
        return societeAnnonceurRepository.save(societe);
    }

    public void delete(Long id) {
        societeAnnonceurRepository.deleteById(id);
    }
}
