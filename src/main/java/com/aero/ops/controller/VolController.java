package com.aero.ops.controller;

import com.aero.ops.model.Vol;
import com.aero.ops.model.VolDetail;
import com.aero.ops.service.AvionService;
import com.aero.ops.service.AeroportService;
import com.aero.ops.service.CompagnieService;
import com.aero.ops.service.VolDetailService;
import com.aero.ops.service.VolService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vol")
public class VolController {

    private final VolService volService;
    private final VolDetailService volDetailService;
    private final CompagnieService compagnieService;
    private final AeroportService aeroportService;
    private final AvionService avionService;

    public VolController(VolService volService,
                         VolDetailService volDetailService,
                         CompagnieService compagnieService,
                         AeroportService aeroportService,
                         AvionService avionService) {
        this.volService = volService;
        this.volDetailService = volDetailService;
        this.compagnieService = compagnieService;
        this.aeroportService = aeroportService;
        this.avionService = avionService;
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

    // Détails et exécutions d’un vol
    @GetMapping("/{id}/details")
    public String volDetails(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        model.addAttribute("vol", vol);
        model.addAttribute("details", volDetailService.getByVol(vol));
        model.addAttribute("newDetail", new VolDetail());
        model.addAttribute("avions", avionService.getAll());
        return "views/vol/details";
    }

    // Recette maximale possible pour un vol (somme des exécutions)
    @GetMapping("/{id}/max-revenue")
    public String maxRevenue(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        java.util.List<VolDetail> details = volDetailService.getByVol(vol);
        double total = details.stream().mapToDouble(d -> d.getMaxRevenue() != null ? d.getMaxRevenue() : 0.0).sum();
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
        java.time.LocalDateTime parsedStart = null;
        java.time.LocalDateTime parsedEnd = null;
        try {
            if (startDateStr != null && !startDateStr.isBlank()) {
                java.time.LocalDate sd = java.time.LocalDate.parse(startDateStr);
                parsedStart = sd.atStartOfDay();
            }
            if (endDateStr != null && !endDateStr.isBlank()) {
                java.time.LocalDate ed = java.time.LocalDate.parse(endDateStr);
                parsedEnd = ed.atTime(23,59,59,999000000);
            }
        } catch (java.time.format.DateTimeParseException ex) {
            // ignore invalid parse, leave filters null
        }

        final java.time.LocalDateTime start = parsedStart;
        final java.time.LocalDateTime end = parsedEnd;
        java.util.List<Vol> vols = volService.getAll();
        java.util.Map<Long, Double> totals = new java.util.HashMap<>();
        for (Vol v : vols) {
            java.util.List<VolDetail> details = volDetailService.getByVol(v);
            double sum = details.stream()
                    .filter(d -> (start == null || (d.getDateHeureDepart() != null && !d.getDateHeureDepart().isBefore(start)))
                            && (end == null || (d.getDateHeureDepart() != null && !d.getDateHeureDepart().isAfter(end)))
                            && (avionId == null || (d.getAvion() != null && d.getAvion().getIdAvion().equals(avionId))))
                    .mapToDouble(d -> d.getMaxRevenue() != null ? d.getMaxRevenue() : 0.0)
                    .sum();
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

    // Ajouter un VolDetail
    @PostMapping("/{id}/details/add")
    public String addVolDetail(@PathVariable Long id, @ModelAttribute VolDetail volDetail) {
        Vol vol = volService.getById(id);
        volDetail.setVol(vol);
        if (volDetail.getAvion() != null && volDetail.getAvion().getIdAvion() != null) {
            volDetail.setAvion(avionService.getById(volDetail.getAvion().getIdAvion()));
        }
        volDetailService.create(volDetail);
        return "redirect:/vol/" + id + "/details";
    }

    // Supprimer un VolDetail
    @GetMapping("/details/delete/{id}")
    public String deleteVolDetail(@PathVariable Long id) {
        VolDetail detail = volDetailService.getById(id);
        Long volId = detail.getVol().getIdVol();
        volDetailService.delete(id);
        return "redirect:/vol/" + volId + "/details";
    }
}
