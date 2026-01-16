package com.aero.ops.service;

import com.aero.ops.model.*;
import com.aero.ops.repository.VolDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VolDetailService {

    private final VolDetailRepository volDetailRepository;
    private final ClasseSiegeService classeSiegeService;
    private final CategorieAgeService categorieAgeService;
    private final VolClasseService volClasseService;
    private final PrixClasseAgeService prixClasseAgeService;

    public VolDetailService(VolDetailRepository volDetailRepository,
                            ClasseSiegeService classeSiegeService,
                            CategorieAgeService categorieAgeService,
                            VolClasseService volClasseService,
                            PrixClasseAgeService prixClasseAgeService) {
        this.volDetailRepository = volDetailRepository;
        this.classeSiegeService = classeSiegeService;
        this.categorieAgeService = categorieAgeService;
        this.volClasseService = volClasseService;
        this.prixClasseAgeService = prixClasseAgeService;
    }

    public List<VolDetail> getAll() {
        return volDetailRepository.findAll();
    }

    public VolDetail getById(Long id) {
        return volDetailRepository.findById(id).orElse(null);
    }

    public List<VolDetail> getByVol(Vol vol) {
        return volDetailRepository.findByVol(vol);
    }

    public List<VolDetail> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return volDetailRepository.findByDateHeureDepartBetween(start, end);
    }

    /**
     * Crée un VolDetail simple (sans classes ni prix).
     * Utilisez createWithClassesAndPrix pour une création complète.
     */
    public VolDetail create(VolDetail volDetail) {
        return volDetailRepository.save(volDetail);
    }

    /**
     * Crée un VolDetail avec les places par classe et les prix par classe/age.
     * 
     * @param volDetail L'entité VolDetail à créer
     * @param placesParClasse Map idClasse -> nombre de places
     * @param prixParClasseAge Map "idClasse_idCategorie" -> prix
     */
    @Transactional
    public VolDetail createWithClassesAndPrix(VolDetail volDetail, 
                                               Map<Long, Integer> placesParClasse,
                                               Map<String, BigDecimal> prixParClasseAge) {
        // Sauvegarder d'abord le VolDetail
        VolDetail saved = volDetailRepository.save(volDetail);
        
        // Créer les entrées VolClasse (places par classe)
        List<VolClasse> volClasses = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : placesParClasse.entrySet()) {
            Long idClasse = entry.getKey();
            Integer places = entry.getValue();
            
            if (places != null && places > 0) {
                ClasseSiege classe = classeSiegeService.getById(idClasse);
                if (classe != null) {
                    VolClasse vc = new VolClasse();
                    vc.setVolDetail(saved);
                    vc.setClasseSiege(classe);
                    vc.setPlacesRestantes(places);
                    volClasseService.save(vc);
                    volClasses.add(vc);
                }
            }
        }
        
        // Créer les entrées PrixClasseAge
        for (Map.Entry<String, BigDecimal> entry : prixParClasseAge.entrySet()) {
            String key = entry.getKey(); // Format: "idClasse_idCategorie"
            BigDecimal prix = entry.getValue();
            
            if (prix != null && prix.compareTo(BigDecimal.ZERO) >= 0) {
                String[] parts = key.split("_");
                if (parts.length == 2) {
                    Long idClasse = Long.parseLong(parts[0]);
                    Long idCategorie = Long.parseLong(parts[1]);
                    
                    ClasseSiege classe = classeSiegeService.getById(idClasse);
                    CategorieAge categorie = categorieAgeService.getById(idCategorie);
                    
                    if (classe != null && categorie != null) {
                        PrixClasseAge pca = new PrixClasseAge();
                        pca.setVolDetail(saved);
                        pca.setClasseSiege(classe);
                        pca.setCategorieAge(categorie);
                        pca.setPrix(prix);
                        prixClasseAgeService.save(pca);
                    }
                }
            }
        }
        
        saved.setVolClasses(volClasses);
        return saved;
    }

    /**
     * Initialise les places restantes depuis les capacités de l'avion.
     */
    @Transactional
    public void initializePlacesFromAvion(VolDetail volDetail) {
        if (volDetail.getAvion() == null || volDetail.getAvion().getAvionClasses() == null) {
            return;
        }
        
        for (AvionClasse ac : volDetail.getAvion().getAvionClasses()) {
            VolClasse vc = new VolClasse();
            vc.setVolDetail(volDetail);
            vc.setClasseSiege(ac.getClasseSiege());
            vc.setPlacesRestantes(ac.getCapacite());
            volClasseService.save(vc);
        }
    }

    public VolDetail update(VolDetail volDetail) {
        return volDetailRepository.save(volDetail);
    }

    @Transactional
    public void delete(Long id) {
        volDetailRepository.deleteById(id);
    }

    /**
     * Retourne toutes les classes de siège disponibles.
     */
    public List<ClasseSiege> getAllClasses() {
        return classeSiegeService.getAll();
    }

    /**
     * Retourne toutes les catégories d'âge.
     */
    public List<CategorieAge> getAllCategoriesAge() {
        return categorieAgeService.getAll();
    }

    /**
     * Récupère les places restantes pour une classe donnée.
     */
    public int getPlacesRestantes(Long idVolDetail, Long idClasse) {
        return volClasseService.getPlacesRestantes(idVolDetail, idClasse);
    }

    /**
     * Récupère le prix pour une classe et une catégorie d'âge.
     */
    public BigDecimal getPrix(Long idVolDetail, Long idClasse, Long idCategorie) {
        return prixClasseAgeService.getMontant(idVolDetail, idClasse, idCategorie);
    }
}
