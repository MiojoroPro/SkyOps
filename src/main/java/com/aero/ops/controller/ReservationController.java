package com.aero.ops.controller;

import com.aero.ops.model.*;
import com.aero.ops.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UtilisateurService utilisateurService;
    private final PaiementService paiementService;
    private final VolDetailService volDetailService;
    private final VolService volService;
    private final ClasseSiegeService classeSiegeService;
    private final CategorieAgeService categorieAgeService;
    private final VolClasseService volClasseService;
    private final PrixClasseAgeService prixClasseAgeService;

    public ReservationController(ReservationService reservationService,
                                 UtilisateurService utilisateurService,
                                 PaiementService paiementService,
                                 VolDetailService volDetailService,
                                 VolService volService,
                                 ClasseSiegeService classeSiegeService,
                                 CategorieAgeService categorieAgeService,
                                 VolClasseService volClasseService,
                                 PrixClasseAgeService prixClasseAgeService) {
        this.reservationService = reservationService;
        this.utilisateurService = utilisateurService;
        this.paiementService = paiementService;
        this.volDetailService = volDetailService;
        this.volService = volService;
        this.classeSiegeService = classeSiegeService;
        this.categorieAgeService = categorieAgeService;
        this.volClasseService = volClasseService;
        this.prixClasseAgeService = prixClasseAgeService;
    }

    // Show booking form for a specific VolDetail
    @GetMapping("/book/{detailId}")
    public String bookForm(@PathVariable Long detailId, Model model) {
        VolDetail detail = volDetailService.getById(detailId);
        if (detail == null) {
            return "redirect:/vol";
        }
        model.addAttribute("detail", detail);
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("classes", classeSiegeService.getAll());
        model.addAttribute("categories", categorieAgeService.getAll());
        // Pass available places per class
        model.addAttribute("volClasses", volClasseService.getByVolDetail(detailId));
        // Pass prices per class/age
        model.addAttribute("prixClasses", prixClasseAgeService.getByVolDetail(detailId));
        return "views/reservation/book";
    }

    // Process booking
    @PostMapping("/book/{detailId}")
    @Transactional
    public String book(@PathVariable Long detailId,
                       @RequestParam("classeSiegeId") Long classeSiegeId,
                       @RequestParam("categorieAgeId") Long categorieAgeId,
                       @RequestParam(value = "utilisateurId", required = false) Long utilisateurId,
                       @RequestParam(value = "payerMaintenant", required = false) boolean payerMaintenant,
                       Model model) {
        VolDetail detail = volDetailService.getById(detailId);
        if (detail == null) {
            return "redirect:/vol";
        }
        
        ClasseSiege classeSiege = classeSiegeService.getById(classeSiegeId);
        CategorieAge categorieAge = categorieAgeService.getById(categorieAgeId);
        
        if (classeSiege == null || categorieAge == null) {
            model.addAttribute("error", "Classe ou catégorie invalide");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Check available places
        if (!volClasseService.hasPlacesDisponibles(detailId, classeSiegeId)) {
            model.addAttribute("error", "Plus de places disponibles pour cette classe");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Get the price
        BigDecimal prix = prixClasseAgeService.getMontant(detailId, classeSiegeId, categorieAgeId);
        if (prix == null) {
            model.addAttribute("error", "Prix non défini pour cette combinaison classe/catégorie");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setVolDetail(detail);
        reservation.setClasseSiege(classeSiege);
        reservation.setCategorieAge(categorieAge);
        reservation.setNumeroReservation("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        if (utilisateurId != null) {
            reservation.setUtilisateur(utilisateurService.getById(utilisateurId));
        }
        
        if (payerMaintenant) {
            reservation.setStatut("CONFIRMEE");
        } else {
            reservation.setStatut("EN_ATTENTE");
        }

        try {
            // Create reservation (this decrements available places)
            Reservation saved = reservationService.create(reservation);

            // Create payment record
            Paiement paiement = new Paiement();
            paiement.setMontant(prix);
            if (payerMaintenant) {
                paiement.setStatut("PAYE");
                paiement.setDatePaiement(LocalDateTime.now());
            } else {
                paiement.setStatut("NON_PAYE");
            }
            paiement.setReservation(saved);
            paiementService.create(paiement);

            if (saved.getUtilisateur() != null) {
                return "redirect:/reservations/user/" + saved.getUtilisateur().getIdUtilisateur();
            } else {
                return "redirect:/reservations/" + saved.getIdReservation();
            }
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            return prepareBookFormModel(model, detailId, detail);
        }
    }
    
    private String prepareBookFormModel(Model model, Long detailId, VolDetail detail) {
        model.addAttribute("detail", detail);
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("classes", classeSiegeService.getAll());
        model.addAttribute("categories", categorieAgeService.getAll());
        model.addAttribute("volClasses", volClasseService.getByVolDetail(detailId));
        model.addAttribute("prixClasses", prixClasseAgeService.getByVolDetail(detailId));
        return "views/reservation/book";
    }

    // List reservations (with optional filters)
    @GetMapping
    public String list(@RequestParam(value = "userId", required = false) Long userId,
                       @RequestParam(value = "volId", required = false) Long volId,
                       Model model) {
        if (userId != null) {
            model.addAttribute("reservations", reservationService.getByUtilisateur(userId));
        } else if (volId != null) {
            model.addAttribute("reservations", reservationService.getByVol(volId));
        } else {
            model.addAttribute("reservations", reservationService.getAll());
        }
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("vols", volService.getAll());
        model.addAttribute("selectedUser", userId);
        model.addAttribute("selectedVol", volId);
        return "views/reservation/index";
    }

    // List reservations by utilisateur
    @GetMapping("/user/{userId}")
    public String listByUser(@PathVariable Long userId, Model model) {
        model.addAttribute("reservations", reservationService.getByUtilisateur(userId));
        model.addAttribute("user", utilisateurService.getById(userId));
        return "views/reservation/list-by-user";
    }

    // List reservations by vol
    @GetMapping("/vol/{volId}")
    public String listByVol(@PathVariable Long volId, Model model) {
        model.addAttribute("reservations", reservationService.getByVol(volId));
        model.addAttribute("volId", volId);
        return "views/reservation/list-by-vol";
    }

    // Reservation detail
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Reservation r = reservationService.getById(id);
        if (r == null) {
            return "redirect:/reservations";
        }
        model.addAttribute("reservation", r);
        paiementService.getByReservation(id).ifPresent(p -> model.addAttribute("paiement", p));
        return "views/reservation/detail";
    }

    // Pay for a reservation
    @GetMapping("/pay/{id}")
    @Transactional
    public String pay(@PathVariable Long id) {
        Reservation r = reservationService.getById(id);
        if (r == null) {
            return "redirect:/reservations";
        }
        if ("ANNULÉ".equals(r.getStatut())) {
            return "redirect:/reservations/" + id;
        }

        // Get the price from prix_classe_age
        BigDecimal montant = reservationService.getPrix(r);
        if (montant == null) {
            montant = BigDecimal.ZERO;
        }

        final BigDecimal finalMontant = montant;
        
        // If a paiement exists, update it; otherwise create
        paiementService.getByReservation(id).ifPresentOrElse(p -> {
            p.setStatut("PAYE");
            p.setDatePaiement(LocalDateTime.now());
            p.setMontant(finalMontant);
            paiementService.update(p);
        }, () -> {
            Paiement p = new Paiement();
            p.setMontant(finalMontant);
            p.setStatut("PAYE");
            p.setDatePaiement(LocalDateTime.now());
            p.setReservation(r);
            paiementService.create(p);
        });

        r.setStatut("CONFIRMEE");
        reservationService.update(r);
        return "redirect:/reservations/" + id;
    }

    // Cancel a reservation (restore places, refund if paid)
    @GetMapping("/cancel/{id}")
    @Transactional
    public String cancel(@PathVariable Long id) {
        Reservation r = reservationService.getById(id);
        if (r == null) {
            return "redirect:/reservations";
        }
        
        // Use the cancel method which restores places
        reservationService.cancel(r);
        
        paiementService.getByReservation(id).ifPresent(p -> {
            if ("PAYE".equals(p.getStatut())) {
                p.setStatut("REMBOURSE");
                p.setDatePaiement(LocalDateTime.now());
                paiementService.update(p);
            }
        });
        return "redirect:/reservations/" + id;
    }
}
