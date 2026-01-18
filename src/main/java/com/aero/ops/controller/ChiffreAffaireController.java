package com.aero.ops.controller;

import com.aero.ops.model.Paiement;
import com.aero.ops.model.Reservation;
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
    private final ReservationService reservationService;
    private final VolService volService;
    private final UtilisateurService utilisateurService;
    private final AvionService avionService;
    private final CompagnieService compagnieService;

    public ChiffreAffaireController(PaiementService paiementService,
                                    ReservationService reservationService,
                                    VolService volService,
                                    UtilisateurService utilisateurService,
                                    AvionService avionService,
                                    CompagnieService compagnieService) {
        this.paiementService = paiementService;
        this.reservationService = reservationService;
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

    /**
     * CA Prévisionnel - basé sur toutes les réservations (payées ou non)
     */
    @GetMapping("/previsionnel")
    public String previsionnel(
            @RequestParam(value = "volId", required = false) Long volId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "avionId", required = false) Long avionId,
            @RequestParam(value = "compagnieId", required = false) Long compagnieId,
            @RequestParam(value = "statut", required = false) String statut,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        List<Reservation> reservations = reservationService.findByFilters(volId, userId, avionId, compagnieId, statut, startDate, endDate);
        
        // Calcul du CA prévisionnel basé sur le prix de chaque réservation
        BigDecimal totalPrevisionnel = BigDecimal.ZERO;
        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalNonPaye = BigDecimal.ZERO;
        
        for (Reservation r : reservations) {
            BigDecimal prix = reservationService.getPrix(r);
            totalPrevisionnel = totalPrevisionnel.add(prix);
            
            if (r.getPaiement() != null && "PAYE".equals(r.getPaiement().getStatut())) {
                totalPaye = totalPaye.add(prix);
            } else {
                totalNonPaye = totalNonPaye.add(prix);
            }
        }
        
        int nbReservations = reservations.size();
        long nbPayees = reservations.stream().filter(r -> r.getPaiement() != null && "PAYE".equals(r.getPaiement().getStatut())).count();
        long nbNonPayees = nbReservations - nbPayees;
        BigDecimal moyenne = nbReservations > 0 ? totalPrevisionnel.divide(BigDecimal.valueOf(nbReservations), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

        model.addAttribute("reservations", reservations);
        model.addAttribute("totalPrevisionnel", totalPrevisionnel);
        model.addAttribute("totalPaye", totalPaye);
        model.addAttribute("totalNonPaye", totalNonPaye);
        model.addAttribute("nbReservations", nbReservations);
        model.addAttribute("nbPayees", nbPayees);
        model.addAttribute("nbNonPayees", nbNonPayees);
        model.addAttribute("moyenne", moyenne);
        model.addAttribute("vols", volService.getAll());
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("selectedVol", volId);
        model.addAttribute("selectedUser", userId);
        model.addAttribute("selectedAvion", avionId);
        model.addAttribute("selectedCompagnie", compagnieId);
        model.addAttribute("selectedStatut", statut);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "views/chiffre-affaire/previsionnel";
    }
}
