package com.aero.ops.service;

import com.aero.ops.model.*;
import com.aero.ops.repository.VolDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class VolDetailService {

    private final VolDetailRepository volDetailRepository;
    private final ClasseSiegeService classeSiegeService;
    private final CategorieAgeService categorieAgeService;
    private final PrixClasseService prixClasseService;
    private final RemiseService remiseService;

    public VolDetailService(VolDetailRepository volDetailRepository,
                            ClasseSiegeService classeSiegeService,
                            CategorieAgeService categorieAgeService,
                            PrixClasseService prixClasseService,
                            RemiseService remiseService) {
        this.volDetailRepository = volDetailRepository;
        this.classeSiegeService = classeSiegeService;
        this.categorieAgeService = categorieAgeService;
        this.prixClasseService = prixClasseService;
        this.remiseService = remiseService;
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
     * Calcule le chiffre d'affaires réel pour un VolDetail.
     * Prend en compte les remises selon la catégorie d'âge du passager.
     */
    public BigDecimal calculerChiffreAffairesReel(VolDetail volDetail) {
        if (volDetail == null || volDetail.getReservations() == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (Reservation res : volDetail.getReservations()) {
            if (res.getStatut() != null && 
                (res.getStatut().equals("CONFIRMEE") || res.getStatut().equals("EN_ATTENTE"))) {
                
                Long idClasse = res.getClasseSiege() != null ? res.getClasseSiege().getIdClasse() : null;
                Long idCategorie = res.getCategorieAge() != null ? res.getCategorieAge().getIdCategorie() : null;
                
                if (idClasse != null && idCategorie != null) {
                    BigDecimal prix = getPrixFinal(volDetail.getIdVolDetail(), idClasse, idCategorie);
                    total = total.add(prix);
                }
            }
        }
        return total;
    }

    /**
     * Crée un VolDetail simple (sans classes ni prix).
     * Utilisez createWithClassesAndPrix pour une création complète.
     */
    public VolDetail create(VolDetail volDetail) {
        return volDetailRepository.save(volDetail);
    }

    /**
     * Crée un VolDetail avec les prix de base par classe.
     * Les places restantes sont calculées dynamiquement à partir des réservations.
     * 
     * @param volDetail L'entité VolDetail à créer
     * @param prixParClasse Map idClasse -> prix de base
     */
    @Transactional
    public VolDetail createWithPrix(VolDetail volDetail, 
                                     Map<Long, BigDecimal> prixParClasse) {
        // Sauvegarder d'abord le VolDetail
        VolDetail saved = volDetailRepository.save(volDetail);
        
        // Créer les entrées PrixClasse (prix de base par classe)
        for (Map.Entry<Long, BigDecimal> entry : prixParClasse.entrySet()) {
            Long idClasse = entry.getKey();
            BigDecimal prix = entry.getValue();
            
            if (prix != null && prix.compareTo(BigDecimal.ZERO) >= 0) {
                ClasseSiege classe = classeSiegeService.getById(idClasse);
                if (classe != null) {
                    PrixClasse pc = new PrixClasse();
                    pc.setVolDetail(saved);
                    pc.setClasseSiege(classe);
                    pc.setPrixBase(prix);
                    prixClasseService.save(pc);
                }
            }
        }
        
        return saved;
    }

    /**
     * Crée un VolDetail avec les prix de base par classe (ancien format compatible).
     * Format de prixParClasseAge: "idClasse_idCategorie" -> prix
     * Les prix sont stockés dans prix_classe (ignorant la catégorie d'âge)
     */
    @Transactional
    public VolDetail createWithClassesAndPrix(VolDetail volDetail, 
                                               Map<Long, Integer> placesParClasse,
                                               Map<String, BigDecimal> prixParClasseAge) {
        // Sauvegarder d'abord le VolDetail
        VolDetail saved = volDetailRepository.save(volDetail);
        
        // Créer les entrées PrixClasse à partir du format ancien
        // On prendra le prix pour la première catégorie d'âge comme prix de base
        for (Map.Entry<String, BigDecimal> entry : prixParClasseAge.entrySet()) {
            String key = entry.getKey(); // Format: "idClasse_idCategorie"
            BigDecimal prix = entry.getValue();
            
            if (prix != null && prix.compareTo(BigDecimal.ZERO) >= 0) {
                String[] parts = key.split("_");
                if (parts.length == 2) {
                    Long idClasse = Long.parseLong(parts[0]);
                    
                    // Vérifier si on a déjà créé un prix pour cette classe
                    if (prixClasseService.getByVolDetailAndClasse(saved.getIdVolDetail(), idClasse).isEmpty()) {
                        ClasseSiege classe = classeSiegeService.getById(idClasse);
                        if (classe != null) {
                            PrixClasse pc = new PrixClasse();
                            pc.setVolDetail(saved);
                            pc.setClasseSiege(classe);
                            pc.setPrixBase(prix);
                            prixClasseService.save(pc);
                        }
                    }
                }
            }
        }
        
        return saved;
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
     * Calculé comme: Capacité - Nombre de réservations confirmées/en attente
     */
    public int getPlacesRestantes(Long idVolDetail, Long idClasse) {
        VolDetail volDetail = getById(idVolDetail);
        if (volDetail == null) return 0;
        return volDetail.getPlacesRestantesByClasse(idClasse);
    }

    /**
     * Récupère le prix de base pour une classe.
     */
    public BigDecimal getPrixBase(Long idVolDetail, Long idClasse) {
        return prixClasseService.getPrixBase(idVolDetail, idClasse);
    }

    /**
     * Récupère le prix final avec remise appliquée pour une classe et catégorie.
     * Utilise le service de remise pour calculer correctement le prix réduit.
     */
    public BigDecimal getPrixFinal(Long idVolDetail, Long idClasse, Long idCategorie) {
        BigDecimal prixBase = getPrixBase(idVolDetail, idClasse);
        if (prixBase == null || prixBase.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return remiseService.calculerPrixAvecRemise(prixBase, idClasse, idCategorie);
    }

    /**
     * Calcule le prix avec remise
     */
    public BigDecimal calculerPrixAvecRemise(Long idVolDetail, Long idClasse, Long idCategorie) {
        BigDecimal prixBase = getPrixBase(idVolDetail, idClasse);
        return remiseService.calculerPrixAvecRemise(prixBase, idClasse, idCategorie);
    }
}
