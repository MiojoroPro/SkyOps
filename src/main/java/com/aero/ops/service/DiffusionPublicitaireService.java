package com.aero.ops.service;

import com.aero.ops.model.DiffusionPublicitaire;
import com.aero.ops.repository.DiffusionPublicitaireRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DiffusionPublicitaireService {

    private final DiffusionPublicitaireRepository diffusionPublicitaireRepository;

    public DiffusionPublicitaireService(DiffusionPublicitaireRepository diffusionPublicitaireRepository) {
        this.diffusionPublicitaireRepository = diffusionPublicitaireRepository;
    }

    public List<DiffusionPublicitaire> getAll() {
        return diffusionPublicitaireRepository.findAll();
    }

    public DiffusionPublicitaire getById(Long id) {
        return diffusionPublicitaireRepository.findById(id).orElse(null);
    }

    public List<DiffusionPublicitaire> getByMoisAndAnnee(Integer mois, Integer annee) {
        return diffusionPublicitaireRepository.findByMoisAndAnnee(mois, annee);
    }

    public List<DiffusionPublicitaire> getBySociete(Long idSociete) {
        return diffusionPublicitaireRepository.findBySociete(idSociete);
    }

    public List<DiffusionPublicitaire> getBySocieteAndMoisAndAnnee(Long idSociete, Integer mois, Integer annee) {
        return diffusionPublicitaireRepository.findBySocieteAndMoisAndAnnee(idSociete, mois, annee);
    }

    public List<DiffusionPublicitaire> getByAnnee(Integer annee) {
        return diffusionPublicitaireRepository.findByAnnee(annee);
    }

    public List<DiffusionPublicitaire> getByAvion(Long idAvion) {
        return diffusionPublicitaireRepository.findByAvionIdAvion(idAvion);
    }

    public List<DiffusionPublicitaire> getByAvionAndMoisAndAnnee(Long idAvion, Integer mois, Integer annee) {
        return diffusionPublicitaireRepository.findByAvionIdAvionAndMoisAndAnnee(idAvion, mois, annee);
    }

    public DiffusionPublicitaire create(DiffusionPublicitaire diffusion) {
        return diffusionPublicitaireRepository.save(diffusion);
    }

    public DiffusionPublicitaire update(DiffusionPublicitaire diffusion) {
        return diffusionPublicitaireRepository.save(diffusion);
    }

    public void delete(Long id) {
        diffusionPublicitaireRepository.deleteById(id);
    }

    /**
     * Calcule le chiffre d'affaires total pour un mois et une année donnés
     */
    public BigDecimal calculerCA(Integer mois, Integer annee) {
        List<DiffusionPublicitaire> diffusions = getByMoisAndAnnee(mois, annee);
        return diffusions.stream()
                .map(DiffusionPublicitaire::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule le nombre total de diffusions pour un mois et une année donnés
     */
    public int calculerNombreDiffusions(Integer mois, Integer annee) {
        List<DiffusionPublicitaire> diffusions = getByMoisAndAnnee(mois, annee);
        return diffusions.stream()
                .mapToInt(DiffusionPublicitaire::getNombreDiffusions)
                .sum();
    }

    /**
     * Calcule le CA pour une société, un mois et une année donnés
     */
    public BigDecimal calculerCABySociete(Long idSociete, Integer mois, Integer annee) {
        List<DiffusionPublicitaire> diffusions = getBySocieteAndMoisAndAnnee(idSociete, mois, annee);
        return diffusions.stream()
                .map(DiffusionPublicitaire::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
