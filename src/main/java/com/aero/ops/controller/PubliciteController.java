package com.aero.ops.controller;

import com.aero.ops.model.DiffusionPublicitaire;
import com.aero.ops.model.Publicite;
import com.aero.ops.model.SocieteAnnonceur;
import com.aero.ops.model.TarifPublicitaire;
import com.aero.ops.service.AvionService;
import com.aero.ops.service.DiffusionPublicitaireService;
import com.aero.ops.service.PubliciteService;
import com.aero.ops.service.SocieteAnnonceurService;
import com.aero.ops.service.TarifPublicitaireService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/publicites")
public class PubliciteController {

    private final SocieteAnnonceurService societeAnnonceurService;
    private final PubliciteService publiciteService;
    private final TarifPublicitaireService tarifPublicitaireService;
    private final DiffusionPublicitaireService diffusionPublicitaireService;
    private final AvionService avionService;

    public PubliciteController(SocieteAnnonceurService societeAnnonceurService,
                               PubliciteService publiciteService,
                               TarifPublicitaireService tarifPublicitaireService,
                               DiffusionPublicitaireService diffusionPublicitaireService,
                               AvionService avionService) {
        this.societeAnnonceurService = societeAnnonceurService;
        this.publiciteService = publiciteService;
        this.tarifPublicitaireService = tarifPublicitaireService;
        this.diffusionPublicitaireService = diffusionPublicitaireService;
        this.avionService = avionService;
    }

    // ==================== SOCIETES ANNONCEURS ====================

    @GetMapping("/societes")
    public String listSocietes(Model model) {
        model.addAttribute("societes", societeAnnonceurService.getAll());
        return "views/publicites/societes/index";
    }

    @GetMapping("/societes/new")
    public String newSociete(Model model) {
        model.addAttribute("societe", new SocieteAnnonceur());
        return "views/publicites/societes/form";
    }

    @PostMapping("/societes/save")
    public String saveSociete(@ModelAttribute SocieteAnnonceur societe) {
        societeAnnonceurService.create(societe);
        return "redirect:/publicites/societes";
    }

    @GetMapping("/societes/edit/{id}")
    public String editSociete(@PathVariable Long id, Model model) {
        model.addAttribute("societe", societeAnnonceurService.getById(id));
        return "views/publicites/societes/form";
    }

    @GetMapping("/societes/delete/{id}")
    public String deleteSociete(@PathVariable Long id) {
        societeAnnonceurService.delete(id);
        return "redirect:/publicites/societes";
    }

    // ==================== PUBLICITES ====================

    @GetMapping
    public String listPublicites(Model model) {
        model.addAttribute("publicites", publiciteService.getAll());
        return "views/publicites/index";
    }

    @GetMapping("/new")
    public String newPublicite(Model model) {
        model.addAttribute("publicite", new Publicite());
        model.addAttribute("societes", societeAnnonceurService.getAll());
        return "views/publicites/form";
    }

    @PostMapping("/save")
    public String savePublicite(@ModelAttribute Publicite publicite, @RequestParam Long societeId) {
        publicite.setSociete(societeAnnonceurService.getById(societeId));
        publiciteService.create(publicite);
        return "redirect:/publicites";
    }

    @GetMapping("/edit/{id}")
    public String editPublicite(@PathVariable Long id, Model model) {
        model.addAttribute("publicite", publiciteService.getById(id));
        model.addAttribute("societes", societeAnnonceurService.getAll());
        return "views/publicites/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePublicite(@PathVariable Long id) {
        publiciteService.delete(id);
        return "redirect:/publicites";
    }

    // ==================== TARIFS ====================

    @GetMapping("/tarifs")
    public String listTarifs(Model model) {
        model.addAttribute("tarifs", tarifPublicitaireService.getAll());
        return "views/publicites/tarifs/index";
    }

    @GetMapping("/tarifs/new")
    public String newTarif(Model model) {
        model.addAttribute("tarif", new TarifPublicitaire());
        return "views/publicites/tarifs/form";
    }

    @PostMapping("/tarifs/save")
    public String saveTarif(@ModelAttribute TarifPublicitaire tarif) {
        tarifPublicitaireService.create(tarif);
        return "redirect:/publicites/tarifs";
    }

    @GetMapping("/tarifs/edit/{id}")
    public String editTarif(@PathVariable Long id, Model model) {
        model.addAttribute("tarif", tarifPublicitaireService.getById(id));
        return "views/publicites/tarifs/form";
    }

    @GetMapping("/tarifs/delete/{id}")
    public String deleteTarif(@PathVariable Long id) {
        tarifPublicitaireService.delete(id);
        return "redirect:/publicites/tarifs";
    }

    // ==================== DIFFUSIONS ====================

    @GetMapping("/diffusions")
    public String listDiffusions(
            @RequestParam(value = "mois", required = false) Integer mois,
            @RequestParam(value = "annee", required = false) Integer annee,
            @RequestParam(value = "societeId", required = false) Long societeId,
            @RequestParam(value = "avionId", required = false) Long avionId,
            Model model) {

        List<DiffusionPublicitaire> diffusions = diffusionPublicitaireService.getAll();
        
        // Filtrage par avion
        if (avionId != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getAvion() != null && d.getAvion().getIdAvion().equals(avionId))
                    .collect(Collectors.toList());
        }
        
        // Filtrage par société
        if (societeId != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getPublicite() != null && d.getPublicite().getSociete() != null 
                            && d.getPublicite().getSociete().getIdSociete().equals(societeId))
                    .collect(Collectors.toList());
        }
        
        // Filtrage par mois
        if (mois != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getMois().equals(mois))
                    .collect(Collectors.toList());
        }
        
        // Filtrage par année
        if (annee != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getAnnee().equals(annee))
                    .collect(Collectors.toList());
        }

        // Calcul du CA total
        BigDecimal totalCA = diffusions.stream()
                .map(DiffusionPublicitaire::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcul du nombre total de diffusions
        int totalDiffusions = diffusions.stream()
                .mapToInt(DiffusionPublicitaire::getNombreDiffusions)
                .sum();

        model.addAttribute("diffusions", diffusions);
        model.addAttribute("societes", societeAnnonceurService.getAll());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("selectedMois", mois);
        model.addAttribute("selectedAnnee", annee);
        model.addAttribute("selectedSociete", societeId);
        model.addAttribute("selectedAvion", avionId);
        model.addAttribute("totalCA", totalCA);
        model.addAttribute("totalDiffusions", totalDiffusions);
        model.addAttribute("nbSocietes", diffusions.stream()
                .filter(d -> d.getPublicite() != null && d.getPublicite().getSociete() != null)
                .map(d -> d.getPublicite().getSociete().getIdSociete())
                .distinct()
                .count());

        return "views/publicites/diffusions/index";
    }

    @GetMapping("/diffusions/new")
    public String newDiffusion(Model model) {
        model.addAttribute("diffusion", new DiffusionPublicitaire());
        model.addAttribute("publicites", publiciteService.getAll());
        model.addAttribute("tarifs", tarifPublicitaireService.getAll());
        model.addAttribute("avions", avionService.getAll());
        return "views/publicites/diffusions/form";
    }

    @PostMapping("/diffusions/save")
    public String saveDiffusion(@ModelAttribute DiffusionPublicitaire diffusion,
                                @RequestParam Long publiciteId,
                                @RequestParam Long tarifId,
                                @RequestParam Long avionId) {
        diffusion.setPublicite(publiciteService.getById(publiciteId));
        diffusion.setTarif(tarifPublicitaireService.getById(tarifId));
        diffusion.setAvion(avionService.getById(avionId));
        diffusionPublicitaireService.create(diffusion);
        return "redirect:/publicites/diffusions";
    }

    @GetMapping("/diffusions/edit/{id}")
    public String editDiffusion(@PathVariable Long id, Model model) {
        model.addAttribute("diffusion", diffusionPublicitaireService.getById(id));
        model.addAttribute("publicites", publiciteService.getAll());
        model.addAttribute("tarifs", tarifPublicitaireService.getAll());
        model.addAttribute("avions", avionService.getAll());
        return "views/publicites/diffusions/form";
    }

    @GetMapping("/diffusions/delete/{id}")
    public String deleteDiffusion(@PathVariable Long id) {
        diffusionPublicitaireService.delete(id);
        return "redirect:/publicites/diffusions";
    }

    // ==================== CHIFFRE D'AFFAIRES PUBLICITAIRE ====================

    @GetMapping("/ca")
    public String chiffreAffaire(
            @RequestParam(value = "mois", required = false) Integer mois,
            @RequestParam(value = "annee", required = false) Integer annee,
            @RequestParam(value = "societeId", required = false) Long societeId,
            @RequestParam(value = "avionId", required = false) Long avionId,
            Model model) {

        List<DiffusionPublicitaire> diffusions = diffusionPublicitaireService.getAll();
        
        // Filtrage par avion
        if (avionId != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getAvion() != null && d.getAvion().getIdAvion().equals(avionId))
                    .collect(Collectors.toList());
        }
        
        // Filtrage par société
        if (societeId != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getPublicite() != null && d.getPublicite().getSociete() != null 
                            && d.getPublicite().getSociete().getIdSociete().equals(societeId))
                    .collect(Collectors.toList());
        }
        
        // Filtrage par mois
        if (mois != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getMois().equals(mois))
                    .collect(Collectors.toList());
        }
        
        // Filtrage par année
        if (annee != null) {
            diffusions = diffusions.stream()
                    .filter(d -> d.getAnnee().equals(annee))
                    .collect(Collectors.toList());
        }

        // Calcul du CA total
        BigDecimal totalCA = diffusions.stream()
                .map(DiffusionPublicitaire::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcul du nombre total de diffusions
        int totalDiffusions = diffusions.stream()
                .mapToInt(DiffusionPublicitaire::getNombreDiffusions)
                .sum();

        // Calcul du CA moyen par diffusion
        BigDecimal moyenneParDiffusion = totalDiffusions > 0 
                ? totalCA.divide(BigDecimal.valueOf(totalDiffusions), 2, java.math.RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        model.addAttribute("diffusions", diffusions);
        model.addAttribute("societes", societeAnnonceurService.getAll());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("selectedMois", mois);
        model.addAttribute("selectedAnnee", annee);
        model.addAttribute("selectedSociete", societeId);
        model.addAttribute("selectedAvion", avionId);
        model.addAttribute("totalCA", totalCA);
        model.addAttribute("totalDiffusions", totalDiffusions);
        model.addAttribute("moyenneParDiffusion", moyenneParDiffusion);
        model.addAttribute("nbSocietes", diffusions.stream()
                .filter(d -> d.getPublicite() != null && d.getPublicite().getSociete() != null)
                .map(d -> d.getPublicite().getSociete().getIdSociete())
                .distinct()
                .count());

        return "views/publicites/ca/index";
    }
}
