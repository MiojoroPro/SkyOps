package com.aero.ops.controller;

import com.aero.ops.model.*;
import com.aero.ops.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
@RequestMapping("/vol")
public class VolController {

    private final VolService volService;
    private final VolDetailService volDetailService;
    private final CompagnieService compagnieService;
    private final AeroportService aeroportService;
    private final AvionService avionService;
    private final ClasseSiegeService classeSiegeService;
    private final CategorieAgeService categorieAgeService;
    private final PrixClasseService prixClasseService;
    private final RemiseClasseCategorieService remiseService;
    private final ReservationService reservationService;
    private final PaiementService paiementService;

    public VolController(VolService volService,
                         VolDetailService volDetailService,
                         CompagnieService compagnieService,
                         AeroportService aeroportService,
                         AvionService avionService,
                         ClasseSiegeService classeSiegeService,
                         CategorieAgeService categorieAgeService,
                         PrixClasseService prixClasseService,
                         RemiseClasseCategorieService remiseService,
                         ReservationService reservationService,
                         PaiementService paiementService) {
        this.volService = volService;
        this.volDetailService = volDetailService;
        this.compagnieService = compagnieService;
        this.aeroportService = aeroportService;
        this.avionService = avionService;
        this.classeSiegeService = classeSiegeService;
        this.categorieAgeService = categorieAgeService;
        this.prixClasseService = prixClasseService;
        this.remiseService = remiseService;
        this.reservationService = reservationService;
        this.paiementService = paiementService;
    }

    // Liste des vols
    @GetMapping
    public String listVols(Model model) {
        model.addAttribute("vols", volService.getAll());
        return "views/vol/list";
    }

    // Ajouter un vol
    @GetMapping("/add")
    public String addVolForm(Model model) {
        model.addAttribute("vol", new Vol());
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("aeroports", aeroportService.getAll());
        return "views/vol/add";
    }

    @PostMapping("/add")
    public String saveVol(@ModelAttribute Vol vol) {
        if (vol.getCompagnie() != null && vol.getCompagnie().getIdCompagnie() != null) {
            vol.setCompagnie(compagnieService.getById(vol.getCompagnie().getIdCompagnie()));
        }
        if (vol.getAeroportDepart() != null && vol.getAeroportDepart().getIdAeroport() != null) {
            vol.setAeroportDepart(aeroportService.getById(vol.getAeroportDepart().getIdAeroport()));
        }
        if (vol.getAeroportArrivee() != null && vol.getAeroportArrivee().getIdAeroport() != null) {
            vol.setAeroportArrivee(aeroportService.getById(vol.getAeroportArrivee().getIdAeroport()));
        }
        volService.create(vol);
        return "redirect:/vol";
    }

    // Modifier un vol
    @GetMapping("/edit/{id}")
    public String editVolForm(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        model.addAttribute("vol", vol);
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("aeroports", aeroportService.getAll());
        return "views/vol/edit";
    }

    @PostMapping("/edit")
    public String updateVol(@ModelAttribute Vol vol) {
        if (vol.getCompagnie() != null && vol.getCompagnie().getIdCompagnie() != null) {
            vol.setCompagnie(compagnieService.getById(vol.getCompagnie().getIdCompagnie()));
        }
        if (vol.getAeroportDepart() != null && vol.getAeroportDepart().getIdAeroport() != null) {
            vol.setAeroportDepart(aeroportService.getById(vol.getAeroportDepart().getIdAeroport()));
        }
        if (vol.getAeroportArrivee() != null && vol.getAeroportArrivee().getIdAeroport() != null) {
            vol.setAeroportArrivee(aeroportService.getById(vol.getAeroportArrivee().getIdAeroport()));
        }
        volService.update(vol);
        return "redirect:/vol";
    }

    // Supprimer un vol
    @GetMapping("/delete/{id}")
    public String deleteVol(@PathVariable Long id) {
        volService.delete(id);
        return "redirect:/vol";
    }

    // Détails et exécutions d'un vol
    @GetMapping("/{id}/details")
    public String volDetails(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        List<VolDetail> details = volDetailService.getByVol(vol);
        
        // Calculer les CA réels avec remises pour chaque détail
        Map<Long, BigDecimal> caReelsMap = new HashMap<>();
        for (VolDetail detail : details) {
            BigDecimal caReel = volDetailService.calculerChiffreAffairesReel(detail);
            caReelsMap.put(detail.getIdVolDetail(), caReel);
        }
        
        model.addAttribute("vol", vol);
        model.addAttribute("details", details);
        model.addAttribute("caReelsMap", caReelsMap);
        model.addAttribute("newDetail", new VolDetail());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("classes", classeSiegeService.getAll());
        model.addAttribute("categories", categorieAgeService.getAll());
        return "views/vol/details";
    }

    // Formulaire d'ajout de VolDetail (planification de vol)
    @GetMapping("/{id}/details/add")
    public String addVolDetailForm(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        List<ClasseSiege> classes = classeSiegeService.getAll();
        List<CategorieAge> categories = categorieAgeService.getAll();
        
        // Préparer la matrice de remises pour l'affichage
        Map<Long, Map<Long, java.math.BigDecimal>> matriceRemises = new LinkedHashMap<>();
        for (ClasseSiege classe : classes) {
            Map<Long, java.math.BigDecimal> remisesCategorie = new LinkedHashMap<>();
            for (CategorieAge categorie : categories) {
                java.math.BigDecimal pourcentage = remiseService.getPourcentage(classe.getIdClasse(), categorie.getIdCategorie());
                remisesCategorie.put(categorie.getIdCategorie(), pourcentage);
            }
            matriceRemises.put(classe.getIdClasse(), remisesCategorie);
        }
        
        model.addAttribute("vol", vol);
        model.addAttribute("newDetail", new VolDetail());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("classes", classes);
        model.addAttribute("categories", categories);
        model.addAttribute("matriceRemises", matriceRemises);
        return "views/vol/add-detail";
    }

    // Ajouter un VolDetail avec prix de base par classe
    // Les places restantes sont calculées dynamiquement à partir des réservations
    // Les places utilisent automatiquement la capacité définie dans l'avion
    @PostMapping("/{id}/details/add")
    @Transactional
    public String addVolDetail(@PathVariable Long id,
                               @ModelAttribute VolDetail volDetail,
                               @RequestParam Map<String, String> allParams) {
        Vol vol = volService.getById(id);
        volDetail.setVol(vol);
        
        // Résoudre l'avion
        if (volDetail.getAvion() != null && volDetail.getAvion().getIdAvion() != null) {
            volDetail.setAvion(avionService.getById(volDetail.getAvion().getIdAvion()));
        }
        
        // Sauvegarder le VolDetail
        VolDetail saved = volDetailService.create(volDetail);
        
        // Créer les entrées PrixClasse (prix de base par classe)
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            String prixBaseKey = "prix_adulte_" + classe.getIdClasse();
            String prixBaseStr = allParams.get(prixBaseKey);
            
            if (prixBaseStr != null && !prixBaseStr.isBlank()) {
                try {
                    BigDecimal prixBase = new BigDecimal(prixBaseStr);
                    if (prixBase.compareTo(BigDecimal.ZERO) >= 0) {
                        PrixClasse pc = new PrixClasse();
                        pc.setVolDetail(saved);
                        pc.setClasseSiege(classe);
                        pc.setPrixBase(prixBase);
                        prixClasseService.save(pc);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        // Créer les entrées RemiseClasseCategorie si fournies
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            for (CategorieAge categorie : categorieAgeService.getAll()) {
                String remiseKey = "remise_" + classe.getIdClasse() + "_" + categorie.getIdCategorie();
                String remiseStr = allParams.get(remiseKey);
                
                if (remiseStr != null && !remiseStr.isBlank()) {
                    try {
                        BigDecimal pourcentage = new BigDecimal(remiseStr);
                        if (pourcentage.compareTo(BigDecimal.ZERO) >= 0 && pourcentage.compareTo(BigDecimal.valueOf(100)) <= 100) {
                            remiseService.save(classe.getIdClasse(), categorie.getIdCategorie(), pourcentage);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        
        return "redirect:/vol/" + id + "/details";
    }

    // Formulaire de modification d'un VolDetail
    @GetMapping("/details/edit/{id}")
    public String editVolDetailForm(@PathVariable Long id, Model model) {
        VolDetail volDetail = volDetailService.getById(id);
        if (volDetail == null) {
            return "redirect:/vol";
        }
        
        Vol vol = volDetail.getVol();
        List<ClasseSiege> classes = classeSiegeService.getAll();
        List<CategorieAge> categories = categorieAgeService.getAll();
        
        // Récupérer les prix actuels par classe
        Map<Long, BigDecimal> prixActuels = new HashMap<>();
        for (ClasseSiege classe : classes) {
            BigDecimal prix = prixClasseService.getPrixBase(id, classe.getIdClasse());
            prixActuels.put(classe.getIdClasse(), prix);
        }
        
        // Préparer la matrice de remises pour l'affichage
        Map<Long, Map<Long, BigDecimal>> matriceRemises = new LinkedHashMap<>();
        for (ClasseSiege classe : classes) {
            Map<Long, BigDecimal> remisesCategorie = new LinkedHashMap<>();
            for (CategorieAge categorie : categories) {
                BigDecimal pourcentage = remiseService.getPourcentage(classe.getIdClasse(), categorie.getIdCategorie());
                remisesCategorie.put(categorie.getIdCategorie(), pourcentage);
            }
            matriceRemises.put(classe.getIdClasse(), remisesCategorie);
        }
        
        model.addAttribute("vol", vol);
        model.addAttribute("volDetail", volDetail);
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("classes", classes);
        model.addAttribute("categories", categories);
        model.addAttribute("prixActuels", prixActuels);
        model.addAttribute("matriceRemises", matriceRemises);
        return "views/vol/edit-detail";
    }

    // Modifier un VolDetail avec mise à jour des prix et des paiements
    @PostMapping("/details/edit/{id}")
    @Transactional
    public String updateVolDetail(@PathVariable Long id,
                                  @ModelAttribute VolDetail volDetail,
                                  @RequestParam Map<String, String> allParams) {
        VolDetail existing = volDetailService.getById(id);
        if (existing == null) {
            return "redirect:/vol";
        }
        
        Long volId = existing.getVol().getIdVol();
        
        // Mettre à jour les champs de base
        existing.setDateHeureDepart(volDetail.getDateHeureDepart());
        existing.setDateHeureArrivee(volDetail.getDateHeureArrivee());
        existing.setStatut(volDetail.getStatut());
        
        // Résoudre et mettre à jour l'avion
        if (volDetail.getAvion() != null && volDetail.getAvion().getIdAvion() != null) {
            existing.setAvion(avionService.getById(volDetail.getAvion().getIdAvion()));
        }
        
        // Sauvegarder les modifications du VolDetail
        volDetailService.update(existing);
        
        // Mettre à jour les prix par classe et recalculer les paiements
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            String prixBaseKey = "prix_adulte_" + classe.getIdClasse();
            String prixBaseStr = allParams.get(prixBaseKey);
            
            if (prixBaseStr != null && !prixBaseStr.isBlank()) {
                try {
                    BigDecimal prixBase = new BigDecimal(prixBaseStr);
                    if (prixBase.compareTo(BigDecimal.ZERO) >= 0) {
                        // Vérifier si un prix existe déjà
                        Optional<PrixClasse> existingPrix = prixClasseService.getByVolDetailAndClasse(id, classe.getIdClasse());
                        if (existingPrix.isPresent()) {
                            // Mettre à jour le prix existant
                            PrixClasse pc = existingPrix.get();
                            pc.setPrixBase(prixBase);
                            prixClasseService.save(pc);
                        } else {
                            // Créer un nouveau prix
                            PrixClasse pc = new PrixClasse();
                            pc.setVolDetail(existing);
                            pc.setClasseSiege(classe);
                            pc.setPrixBase(prixBase);
                            prixClasseService.save(pc);
                        }
                        
                        // Mettre à jour les paiements des réservations pour cette classe
                        List<Reservation> reservations = reservationService.getByVolDetailAndClasse(id, classe.getIdClasse());
                        for (Reservation reservation : reservations) {
                            // Ne mettre à jour que les réservations non annulées
                            if (reservation.getStatut() != null && !reservation.getStatut().equals("ANNULE")) {
                                // Recalculer le prix final avec la nouvelle base et la remise
                                BigDecimal nouveauPrixFinal = reservationService.getPrixFinal(reservation);
                                
                                // Mettre à jour le paiement si existant
                                if (reservation.getPaiement() != null) {
                                    Paiement paiement = reservation.getPaiement();
                                    paiement.setMontant(nouveauPrixFinal);
                                    paiementService.update(paiement);
                                }
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        return "redirect:/vol/" + volId + "/details";
    }

    // Supprimer un VolDetail
    @GetMapping("/details/delete/{id}")
    @Transactional
    public String deleteVolDetail(@PathVariable Long id) {
        VolDetail detail = volDetailService.getById(id);
        Long volId = detail.getVol().getIdVol();
        volDetailService.delete(id);
        return "redirect:/vol/" + volId + "/details";
    }

    // Recette maximale possible pour un vol (somme des exécutions)
    @GetMapping("/{id}/max-revenue")
    public String maxRevenue(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        List<VolDetail> details = volDetailService.getByVol(vol);
        BigDecimal total = details.stream()
                .map(d -> d.getMaxRevenue() != null ? d.getMaxRevenue() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("vol", vol);
        model.addAttribute("details", details);
        model.addAttribute("totalMaxRevenue", total);
        return "views/vol/max-revenue";
    }

    // Liste des vols avec leur recette maximale potentielle (avec filtres)
    @GetMapping("/max-revenue")
    public String maxRevenueList(@RequestParam(name = "startDate", required = false) String startDateStr,
                                 @RequestParam(name = "endDate", required = false) String endDateStr,
                                 @RequestParam(name = "avionId", required = false) Long avionId,
                                 Model model) {
        LocalDateTime parsedStart = null;
        LocalDateTime parsedEnd = null;
        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                LocalDate sd = LocalDate.parse(startDateStr);
                parsedStart = sd.atStartOfDay();
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                LocalDate ed = LocalDate.parse(endDateStr);
                parsedEnd = ed.atTime(23,59,59,999000000);
            }
        } catch (DateTimeParseException ex) {
            // ignore invalid parse, leave filters null
        }

        final LocalDateTime start = parsedStart;
        final LocalDateTime end = parsedEnd;
        List<Vol> vols = volService.getAll();
        Map<Long, BigDecimal> totals = new HashMap<>();
        for (Vol v : vols) {
            List<VolDetail> details = volDetailService.getByVol(v);
            BigDecimal sum = details.stream()
                    .filter(d -> (start == null || (d.getDateHeureDepart() != null && !d.getDateHeureDepart().isBefore(start)))
                            && (end == null || (d.getDateHeureDepart() != null && !d.getDateHeureDepart().isAfter(end)))
                            && (avionId == null || (d.getAvion() != null && d.getAvion().getIdAvion().equals(avionId))))
                    .map(d -> d.getMaxRevenue() != null ? d.getMaxRevenue() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totals.put(v.getIdVol(), sum);
        }
        model.addAttribute("vols", vols);
        model.addAttribute("totals", totals);
        model.addAttribute("startDate", startDateStr);
        model.addAttribute("endDate", endDateStr);
        model.addAttribute("avionId", avionId);
        model.addAttribute("avions", avionService.getAll());
        return "views/vol/max-revenue-list";
    }
}
