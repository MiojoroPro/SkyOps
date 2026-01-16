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
    private final VolClasseService volClasseService;
    private final PrixClasseAgeService prixClasseAgeService;

    public VolController(VolService volService,
                         VolDetailService volDetailService,
                         CompagnieService compagnieService,
                         AeroportService aeroportService,
                         AvionService avionService,
                         ClasseSiegeService classeSiegeService,
                         CategorieAgeService categorieAgeService,
                         VolClasseService volClasseService,
                         PrixClasseAgeService prixClasseAgeService) {
        this.volService = volService;
        this.volDetailService = volDetailService;
        this.compagnieService = compagnieService;
        this.aeroportService = aeroportService;
        this.avionService = avionService;
        this.classeSiegeService = classeSiegeService;
        this.categorieAgeService = categorieAgeService;
        this.volClasseService = volClasseService;
        this.prixClasseAgeService = prixClasseAgeService;
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
        model.addAttribute("vol", vol);
        model.addAttribute("details", volDetailService.getByVol(vol));
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
        model.addAttribute("vol", vol);
        model.addAttribute("newDetail", new VolDetail());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("classes", classeSiegeService.getAll());
        model.addAttribute("categories", categorieAgeService.getAll());
        return "views/vol/add-detail";
    }

    // Ajouter un VolDetail avec places par classe et prix par classe/âge
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
        
        // Créer les entrées VolClasse (places par classe)
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            String placesKey = "places_" + classe.getIdClasse();
            String placesStr = allParams.get(placesKey);
            if (placesStr != null && !placesStr.isBlank()) {
                try {
                    int places = Integer.parseInt(placesStr);
                    if (places >= 0) {
                        VolClasse vc = new VolClasse();
                        vc.setVolDetail(saved);
                        vc.setClasseSiege(classe);
                        vc.setPlacesRestantes(places);
                        volClasseService.save(vc);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        // Créer les entrées PrixClasseAge (prix par classe et catégorie d'âge)
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            for (CategorieAge categorie : categorieAgeService.getAll()) {
                String prixKey = "prix_" + classe.getIdClasse() + "_" + categorie.getIdCategorie();
                String prixStr = allParams.get(prixKey);
                if (prixStr != null && !prixStr.isBlank()) {
                    try {
                        BigDecimal prix = new BigDecimal(prixStr);
                        if (prix.compareTo(BigDecimal.ZERO) >= 0) {
                            PrixClasseAge pca = new PrixClasseAge();
                            pca.setVolDetail(saved);
                            pca.setClasseSiege(classe);
                            pca.setCategorieAge(categorie);
                            pca.setPrix(prix);
                            prixClasseAgeService.save(pca);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        
        return "redirect:/vol/" + id + "/details";
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
