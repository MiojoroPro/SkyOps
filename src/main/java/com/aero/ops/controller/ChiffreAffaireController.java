package com.aero.ops.controller;

import com.aero.ops.model.Paiement;
import com.aero.ops.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/chiffre-affaire")
public class ChiffreAffaireController {

    private final PaiementService paiementService;
    private final VolService volService;
    private final UtilisateurService utilisateurService;
    private final AvionService avionService;
    private final CompagnieService compagnieService;

    public ChiffreAffaireController(PaiementService paiementService,
                                    VolService volService,
                                    UtilisateurService utilisateurService,
                                    AvionService avionService,
                                    CompagnieService compagnieService) {
        this.paiementService = paiementService;
        this.volService = volService;
        this.utilisateurService = utilisateurService;
        this.avionService = avionService;
        this.compagnieService = compagnieService;
    }

    @GetMapping
    public String index(
            @RequestParam(value = "volId", required = false) Long volId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "avionId", required = false) Long avionId,
            @RequestParam(value = "compagnieId", required = false) Long compagnieId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Paiement> paiements = paiementService.findPaidByFilters(volId, userId, avionId, compagnieId, startDate, endDate);
        
        BigDecimal total = paiements.stream()
                .map(p -> p.getMontant() != null ? p.getMontant() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int nbPaiements = paiements.size();
        BigDecimal moyenne = nbPaiements > 0 ? total.divide(BigDecimal.valueOf(nbPaiements), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

        model.addAttribute("paiements", paiements);
        model.addAttribute("total", total);
        model.addAttribute("nbPaiements", nbPaiements);
        model.addAttribute("moyenne", moyenne);
        model.addAttribute("vols", volService.getAll());
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("selectedVol", volId);
        model.addAttribute("selectedUser", userId);
        model.addAttribute("selectedAvion", avionId);
        model.addAttribute("selectedCompagnie", compagnieId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "views/chiffre-affaire/index";
    }
}
