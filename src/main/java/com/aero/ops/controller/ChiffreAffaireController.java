package com.aero.ops.controller;

import com.aero.ops.model.Paiement;
import com.aero.ops.service.PaiementService;
import com.aero.ops.service.UtilisateurService;
import com.aero.ops.service.VolService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/chiffre-affaire")
public class ChiffreAffaireController {

    private final PaiementService paiementService;
    private final VolService volService;
    private final UtilisateurService utilisateurService;

    public ChiffreAffaireController(PaiementService paiementService,
                                    VolService volService,
                                    UtilisateurService utilisateurService) {
        this.paiementService = paiementService;
        this.volService = volService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String index(
            @RequestParam(value = "volId", required = false) Long volId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Paiement> paiements = paiementService.findPaidByFilters(volId, userId, startDate, endDate);
        double total = paiements.stream().mapToDouble(p -> p.getMontant()).sum();

        model.addAttribute("paiements", paiements);
        model.addAttribute("total", total);
        model.addAttribute("vols", volService.getAll());
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("selectedVol", volId);
        model.addAttribute("selectedUser", userId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "views/chiffre-affaire/index";
    }
}
