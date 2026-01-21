package com.aero.ops.controller;

import com.aero.ops.model.*;
import com.aero.ops.dto.ClasseVolDTO;
import com.aero.ops.dto.PrixVolDTO;
import com.aero.ops.dto.PrixSimpleDTO;
import com.aero.ops.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    private final PrixClasseService prixClasseService;
    private final RemiseClasseCategorieService remiseService;

    public ReservationController(ReservationService reservationService,
                                 UtilisateurService utilisateurService,
                                 PaiementService paiementService,
                                 VolDetailService volDetailService,
                                 VolService volService,
                                 ClasseSiegeService classeSiegeService,
                                 CategorieAgeService categorieAgeService,
                                 PrixClasseService prixClasseService,
                                 RemiseClasseCategorieService remiseService) {
        this.reservationService = reservationService;
        this.utilisateurService = utilisateurService;
        this.paiementService = paiementService;
        this.volDetailService = volDetailService;
        this.volService = volService;
        this.classeSiegeService = classeSiegeService;
        this.categorieAgeService = categorieAgeService;
        this.prixClasseService = prixClasseService;
        this.remiseService = remiseService;
    }

    // Show list of available flights to book
    @GetMapping("/new")
    public String reservationList(Model model) {
        List<VolDetail> volDetails = volDetailService.getAll();
        model.addAttribute("volDetails", volDetails);
        return "views/reservation/select-flight";
    }

    // Show booking form for a specific VolDetail
    @GetMapping("/book/{detailId}")
    public String bookForm(@PathVariable Long detailId, Model model) {
        VolDetail detail = volDetailService.getById(detailId);
        if (detail == null) {
            return "redirect:/reservations/new";
        }
        return prepareBookFormModel(model, detailId, detail);
    }

    // Process booking
    @PostMapping("/book/{detailId}")
    @Transactional
    public String book(@PathVariable Long detailId,
                       @RequestParam(value = "classeSiegeId", required = false) Long classeSiegeId,
                       @RequestParam(value = "categorieAgeId", required = false) Long categorieAgeId,
                       @RequestParam(value = "utilisateurId", required = false) Long utilisateurId,
                       @RequestParam(value = "payerMaintenant", required = false) boolean payerMaintenant,
                       @RequestParam(value = "bookingMode", defaultValue = "single") String bookingMode,
                       @RequestParam(value = "bulkQuantity", defaultValue = "1") int bulkQuantity,
                       @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                       @RequestParam(value = "bulkClasseId", required = false) Long bulkClasseId,
                       Model model) {
        VolDetail detail = volDetailService.getById(detailId);
        if (detail == null) {
            return "redirect:/vol";
        }
        
        // Validate required fields
        if (classeSiegeId == null) {
            model.addAttribute("error", "Veuillez sélectionner une classe de siège");
            return prepareBookFormModel(model, detailId, detail);
        }
        if (categorieAgeId == null) {
            model.addAttribute("error", "Veuillez sélectionner une catégorie de passager");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Use quantity parameter (from UI) - default to bulkQuantity for backward compatibility
        int effectiveQuantity = quantity > 1 ? quantity : bulkQuantity;
        
        // If more than 1 ticket, use bulk booking
        if (effectiveQuantity > 1) {
            return processMultipleBooking(detailId, classeSiegeId, categorieAgeId, utilisateurId, 
                                          payerMaintenant, effectiveQuantity, detail, model);
        }
        
        // Standard single booking
        ClasseSiege classeSiege = classeSiegeService.getById(classeSiegeId);
        CategorieAge categorieAge = categorieAgeService.getById(categorieAgeId);
        
        if (classeSiege == null || categorieAge == null) {
            model.addAttribute("error", "Classe ou catégorie invalide");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Check available places (calculated dynamically: Capacity - Reservations)
        if (!reservationService.hasPlacesDisponibles(detailId, classeSiegeId)) {
            model.addAttribute("error", "Plus de places disponibles pour cette classe");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Get the price with discount applied
        BigDecimal prixFinal = reservationService.getPrixFinal(new Reservation() {{
            setVolDetail(detail);
            setClasseSiege(classeSiege);
            setCategorieAge(categorieAge);
        }});
        
        if (prixFinal == null || prixFinal.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("error", "Prix non défini pour cette combinaison classe/catégorie");
            return prepareBookFormModel(model, detailId, detail);
        }

        // Create reservation
        Reservation reservation = new Reservation();
        reservation.setVolDetail(detail);
        reservation.setClasseSiege(classeSiege);
        reservation.setCategorieAge(categorieAge);
        reservation.setNumeroReservation("RES-" + System.currentTimeMillis());
        
        if (utilisateurId != null) {
            reservation.setUtilisateur(utilisateurService.getById(utilisateurId));
        }
        
        if (payerMaintenant) {
            reservation.setStatut("CONFIRMEE");
        } else {
            reservation.setStatut("EN_ATTENTE");
        }

        try {
            // Create reservation
            Reservation saved = reservationService.create(reservation);

            // Create payment record
            Paiement paiement = new Paiement();
            paiement.setMontant(prixFinal);
            if (payerMaintenant) {
                paiement.setStatut("PAYE");
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
    
    /**
     * Process multiple booking - creates N reservations at once for any category
     */
    private String processMultipleBooking(Long detailId, Long classeSiegeId, Long categorieAgeId,
                                          Long utilisateurId, boolean payerMaintenant, 
                                          int quantity, VolDetail detail, Model model) {
        ClasseSiege classeSiege = classeSiegeService.getById(classeSiegeId);
        if (classeSiege == null) {
            model.addAttribute("error", "Classe invalide");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        CategorieAge categorieAge = categorieAgeService.getById(categorieAgeId);
        if (categorieAge == null) {
            model.addAttribute("error", "Catégorie invalide");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Check if enough places available
        Integer placesRestantes = detail.getPlacesRestantesByClasse(classeSiegeId);
        if (placesRestantes == null || placesRestantes < quantity) {
            model.addAttribute("error", "Pas assez de places disponibles. Places restantes: " + (placesRestantes != null ? placesRestantes : 0));
            return prepareBookFormModel(model, detailId, detail);
        }
        
        // Get the price
        BigDecimal prixUnitaire = reservationService.getPrixFinal(new Reservation() {{
            setVolDetail(detail);
            setClasseSiege(classeSiege);
            setCategorieAge(categorieAge);
        }});
        
        if (prixUnitaire == null || prixUnitaire.compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("error", "Prix non défini pour cette combinaison classe/catégorie");
            return prepareBookFormModel(model, detailId, detail);
        }
        
        Utilisateur utilisateur = utilisateurId != null ? utilisateurService.getById(utilisateurId) : null;
        
        try {
            // Create N reservations
            for (int i = 0; i < quantity; i++) {
                Reservation reservation = new Reservation();
                reservation.setVolDetail(detail);
                reservation.setClasseSiege(classeSiege);
                reservation.setCategorieAge(categorieAge);
                reservation.setNumeroReservation("RES-" + System.currentTimeMillis() + "-" + (i + 1));
                reservation.setUtilisateur(utilisateur);
                reservation.setStatut(payerMaintenant ? "CONFIRMEE" : "EN_ATTENTE");
                
                Reservation saved = reservationService.create(reservation);
                
                // Create payment for each reservation
                Paiement paiement = new Paiement();
                paiement.setMontant(prixUnitaire);
                paiement.setStatut(payerMaintenant ? "PAYE" : "NON_PAYE");
                paiement.setReservation(saved);
                paiementService.create(paiement);
            }
            
            // Redirect to user's reservations or list
            if (utilisateur != null) {
                return "redirect:/reservations/user/" + utilisateur.getIdUtilisateur() + "?success=true&count=" + quantity;
            } else {
                return "redirect:/reservations?success=true&count=" + quantity;
            }
        } catch (IllegalStateException ex) {
            model.addAttribute("error", "Erreur lors de la réservation: " + ex.getMessage());
            return prepareBookFormModel(model, detailId, detail);
        }
    }
    
    private String prepareBookFormModel(Model model, Long detailId, VolDetail detail) {
        model.addAttribute("detail", detail);
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("utilisateurs", utilisateurService.getAll());
        model.addAttribute("categories", categorieAgeService.getAll());
        
        try {
            // Récupérer TOUTES les classes du système
            List<ClasseSiege> allClasses = classeSiegeService.getAll();
            System.out.println("DEBUG: allClasses retrieved: " + (allClasses != null ? allClasses.size() : "null"));
            
            // Créer la liste des classes avec les places restantes
            List<ClasseVolDTO> volClasses = new ArrayList<>();
            if (allClasses != null && !allClasses.isEmpty()) {
                for (ClasseSiege classe : allClasses) {
                    Integer placesRestantes = detail.getPlacesRestantesByClasse(classe.getIdClasse());
                    System.out.println("DEBUG: Classe " + classe.getIdClasse() + " (" + classe.getLibelle() + ") - Places: " + placesRestantes);
                    volClasses.add(new ClasseVolDTO(classe, placesRestantes != null ? placesRestantes : 0));
                }
            } else {
                System.out.println("DEBUG: allClasses is null or empty!");
            }
            model.addAttribute("volClasses", volClasses);
            System.out.println("DEBUG: volClasses added to model: " + volClasses.size());
            
            // Créer la grille tarifaire (classe x catégorie x prix) pour l'affichage HTML
            List<PrixVolDTO> prixClasses = new ArrayList<>();
            // Créer aussi une version simplifiée pour JavaScript (évite les références circulaires)
            List<PrixSimpleDTO> prixSimple = new ArrayList<>();
            
            if (allClasses != null && !allClasses.isEmpty()) {
                for (ClasseSiege classe : allClasses) {
                    BigDecimal prixBase = prixClasseService.getPrixBase(detail.getIdVolDetail(), classe.getIdClasse());
                    System.out.println("DEBUG: Prix base for classe " + classe.getIdClasse() + ": " + prixBase);
                    // Si pas de prix défini, utiliser 0
                    if (prixBase == null) {
                        prixBase = BigDecimal.ZERO;
                    }
                    for (CategorieAge categorie : categorieAgeService.getAll()) {
                        BigDecimal remise = remiseService.getPourcentage(classe.getIdClasse(), categorie.getIdCategorie());
                        BigDecimal prixFinal = prixBase.multiply(BigDecimal.valueOf(100).subtract(remise)).divide(BigDecimal.valueOf(100));
                        prixClasses.add(new PrixVolDTO(classe, categorie, prixFinal));
                        // Version simplifiée pour JavaScript
                        prixSimple.add(new PrixSimpleDTO(classe.getIdClasse(), categorie.getIdCategorie(), prixFinal));
                    }
                }
            }
            model.addAttribute("prixClasses", prixClasses);
            model.addAttribute("prixSimple", prixSimple);
            System.out.println("DEBUG: prixClasses added to model: " + prixClasses.size());
        } catch (Exception e) {
            // Gérer les erreurs
            System.err.println("DEBUG: Exception caught in prepareBookFormModel: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("volClasses", new ArrayList<>());
            model.addAttribute("prixClasses", new ArrayList<>());
            model.addAttribute("prixSimple", new ArrayList<>());
        }
        
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
        
        // Calculate and display the price with discount
        BigDecimal prixFinal = reservationService.getPrixFinal(r);
        model.addAttribute("prixFinal", prixFinal);
        
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

        // Get the price with discount applied
        BigDecimal montant = reservationService.getPrixFinal(r);
        if (montant == null) {
            montant = BigDecimal.ZERO;
        }

        final BigDecimal finalMontant = montant;
        
        // If a paiement exists, update it; otherwise create
        paiementService.getByReservation(id).ifPresentOrElse(p -> {
            p.setStatut("PAYE");
            p.setMontant(finalMontant);
            paiementService.update(p);
        }, () -> {
            Paiement p = new Paiement();
            p.setMontant(finalMontant);
            p.setStatut("PAYE");
            p.setReservation(r);
            paiementService.create(p);
        });

        r.setStatut("CONFIRMEE");
        reservationService.update(r);
        return "redirect:/reservations/" + id;
    }

    // Cancel a reservation (places are freed automatically via dynamic calculation)
    @GetMapping("/cancel/{id}")
    @Transactional
    public String cancel(@PathVariable Long id) {
        Reservation r = reservationService.getById(id);
        if (r == null) {
            return "redirect:/reservations";
        }
        
        // Use the cancel method which marks the reservation as ANNULÉ
        // Places are freed automatically since they're calculated dynamically
        reservationService.cancel(r);
        
        paiementService.getByReservation(id).ifPresent(p -> {
            if ("PAYE".equals(p.getStatut())) {
                p.setStatut("REMBOURSE");
                paiementService.update(p);
            }
        });
        return "redirect:/reservations/" + id;
    }
}
