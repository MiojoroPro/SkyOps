package com.aero.ops.controller;

import com.aero.ops.model.Paiement;
import com.aero.ops.model.Reservation;
import com.aero.ops.model.Utilisateur;
import com.aero.ops.model.VolDetail;
import com.aero.ops.service.PaiementService;
import com.aero.ops.service.ReservationService;
import com.aero.ops.service.UtilisateurService;
import com.aero.ops.service.VolDetailService;
import com.aero.ops.service.VolService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    public ReservationController(ReservationService reservationService,
                                 UtilisateurService utilisateurService,
                                 PaiementService paiementService,
                                 VolDetailService volDetailService,
                                 VolService volService) {
        this.reservationService = reservationService;
        this.utilisateurService = utilisateurService;
        this.paiementService = paiementService;
        this.volDetailService = volDetailService;
        this.volService = volService;
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
        return "views/reservation/book";
    }

    // Process booking (simulation of payment)
    @PostMapping("/book/{detailId}")
    public String book(@PathVariable Long detailId,
                       @ModelAttribute Reservation reservation,
                       @RequestParam(value = "payerMaintenant", required = false) boolean payerMaintenant,
                       Model model) {
        VolDetail detail = volDetailService.getById(detailId);
        if (detail == null) {
            return "redirect:/vol";
        }
        reservation.setVolDetail(detail);
        // Resolve utilisateur by id if set
        if (reservation.getUtilisateur() != null && reservation.getUtilisateur().getIdUtilisateur() != null) {
            reservation.setUtilisateur(utilisateurService.getById(reservation.getUtilisateur().getIdUtilisateur()));
        }
        // Generate a reservation number
        reservation.setNumeroReservation("RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        if (payerMaintenant) {
            reservation.setStatut("CONFIRMEE");
        } else {
            reservation.setStatut("EN_ATTENTE");
        }

        try {
            Reservation saved = reservationService.create(reservation);

            // Create paiement record (simulation) with class-specific price
            Paiement paiement = new Paiement();
            double montant = 0.0;
            if ("ECONOMIQUE".equalsIgnoreCase(reservation.getClasse())) {
                montant = detail.getPrixEconomique() != null ? detail.getPrixEconomique() : 0.0;
            } else if ("PREMIERE".equalsIgnoreCase(reservation.getClasse())) {
                montant = detail.getPrixPremiere() != null ? detail.getPrixPremiere() : 0.0;
            } else if ("PREMIUM".equalsIgnoreCase(reservation.getClasse())) {
                montant = detail.getPrixPremium() != null ? detail.getPrixPremium() : 0.0;
            }
            paiement.setMontant(montant);
            if (payerMaintenant) {
                paiement.setStatut("PAYE");
                paiement.setDatePaiement(LocalDateTime.now());
            } else {
                paiement.setStatut("NON_PAYE");
            }
            paiement.setReservation(saved);
            paiementService.create(paiement);

            return "redirect:/reservations/user/" + saved.getUtilisateur().getIdUtilisateur();
        } catch (IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("detail", detail);
            model.addAttribute("utilisateurs", utilisateurService.getAll());
            return "views/reservation/book";
        }
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

    // Pay for a reservation (simulation)
    @GetMapping("/pay/{id}")
    public String pay(@PathVariable Long id) {
        Reservation r = reservationService.getById(id);
        if (r == null) {
            return "redirect:/reservations";
        }
        if ("ANNULÉ".equals(r.getStatut())) {
            return "redirect:/reservations/" + id;
        }

        // If a paiement exists, update it; otherwise create
        paiementService.getByReservation(id).ifPresentOrElse(p -> {
            p.setStatut("PAYE");
            p.setDatePaiement(LocalDateTime.now());
            if (r.getVolDetail() != null) {
                double montant = 0.0;
                if ("ECONOMIQUE".equalsIgnoreCase(r.getClasse())) {
                    montant = r.getVolDetail().getPrixEconomique() != null ? r.getVolDetail().getPrixEconomique() : 0.0;
                } else if ("PREMIERE".equalsIgnoreCase(r.getClasse())) {
                    montant = r.getVolDetail().getPrixPremiere() != null ? r.getVolDetail().getPrixPremiere() : 0.0;
                } else if ("PREMIUM".equalsIgnoreCase(r.getClasse())) {
                    montant = r.getVolDetail().getPrixPremium() != null ? r.getVolDetail().getPrixPremium() : 0.0;
                }
                p.setMontant(montant);
            }
            paiementService.update(p);
        }, () -> {
            com.aero.ops.model.Paiement p = new com.aero.ops.model.Paiement();
            double montant = 0.0;
            if (r.getVolDetail() != null) {
                if ("ECONOMIQUE".equalsIgnoreCase(r.getClasse())) {
                    montant = r.getVolDetail().getPrixEconomique() != null ? r.getVolDetail().getPrixEconomique() : 0.0;
                } else if ("PREMIERE".equalsIgnoreCase(r.getClasse())) {
                    montant = r.getVolDetail().getPrixPremiere() != null ? r.getVolDetail().getPrixPremiere() : 0.0;
                } else if ("PREMIUM".equalsIgnoreCase(r.getClasse())) {
                    montant = r.getVolDetail().getPrixPremium() != null ? r.getVolDetail().getPrixPremium() : 0.0;
                }
            }
            p.setMontant(montant);
            p.setStatut("PAYE");
            p.setDatePaiement(LocalDateTime.now());
            p.setReservation(r);
            paiementService.create(p);
        });

        r.setStatut("CONFIRMEE");
        reservationService.update(r);
        return "redirect:/reservations/" + id;
    }

    // Cancel a reservation (simulate refund if paid)
    @GetMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        Reservation r = reservationService.getById(id);
        if (r == null) {
            return "redirect:/reservations";
        }
        r.setStatut("ANNULÉ");
        reservationService.update(r);
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