package com.aero.ops.controller;

import com.aero.ops.dto.ChiffreAffaireVolDTO;
import com.aero.ops.model.DiffusionPublicitaire;
import com.aero.ops.model.Paiement;
import com.aero.ops.model.Reservation;
import com.aero.ops.model.VolDetail;
import com.aero.ops.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final VolDetailService volDetailService;
    private final DiffusionPublicitaireService diffusionPublicitaireService;
    private final PaiementPublicitaireService paiementPublicitaireService;
    private final VenteProduitService venteProduitService;

    public ChiffreAffaireController(PaiementService paiementService,
                                    ReservationService reservationService,
                                    VolService volService,
                                    UtilisateurService utilisateurService,
                                    AvionService avionService,
                                    CompagnieService compagnieService,
                                    VolDetailService volDetailService,
                                    DiffusionPublicitaireService diffusionPublicitaireService,
                                    PaiementPublicitaireService paiementPublicitaireService,
                                    VenteProduitService venteProduitService) {
        this.paiementService = paiementService;
        this.reservationService = reservationService;
        this.volService = volService;
        this.utilisateurService = utilisateurService;
        this.avionService = avionService;
        this.compagnieService = compagnieService;
        this.volDetailService = volDetailService;
        this.diffusionPublicitaireService = diffusionPublicitaireService;
        this.paiementPublicitaireService = paiementPublicitaireService;
        this.venteProduitService = venteProduitService;
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

        // Calcul du CA par vol (avec tickets, publicités et produits)
        List<ChiffreAffaireVolDTO> caParVol = calculerCAParVol(startDate, endDate, compagnieId, avionId);
        
        // Totaux globaux
        BigDecimal totalTickets = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantTickets)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPublicites = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantPublicites)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPubPaye = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantPubPaye)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPubRestant = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantPubRestant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Totaux produits vendus
        Integer totalNbProduits = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getNbProduitsVendus)
                .reduce(0, Integer::sum);
        BigDecimal totalMontantProduits = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantProduits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Total CA encaissé = tickets + publicités payées + produits
        BigDecimal totalGlobal = totalTickets.add(totalPubPaye).add(totalMontantProduits);
        // Total CA potentiel = tickets + toutes les publicités + produits
        BigDecimal totalCaPotentiel = totalTickets.add(totalPublicites).add(totalMontantProduits);

        model.addAttribute("paiements", paiements);
        model.addAttribute("total", total);
        model.addAttribute("nbPaiements", nbPaiements);
        model.addAttribute("moyenne", moyenne);
        model.addAttribute("caParVol", caParVol);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalPublicites", totalPublicites);
        model.addAttribute("totalPubPaye", totalPubPaye);
        model.addAttribute("totalPubRestant", totalPubRestant);
        model.addAttribute("totalNbProduits", totalNbProduits);
        model.addAttribute("totalMontantProduits", totalMontantProduits);
        model.addAttribute("totalGlobal", totalGlobal);
        model.addAttribute("totalCaPotentiel", totalCaPotentiel);
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
     * Calcule le CA par vol avec tickets vendus, publicités et produits extra
     */
    private List<ChiffreAffaireVolDTO> calculerCAParVol(LocalDate startDate, LocalDate endDate, Long compagnieId, Long avionId) {
        List<ChiffreAffaireVolDTO> result = new ArrayList<>();
        List<VolDetail> volDetails = volDetailService.getAll();
        
        for (VolDetail vd : volDetails) {
            // Filtrer par date si spécifié
            if (startDate != null && vd.getDateHeureDepart().toLocalDate().isBefore(startDate)) {
                continue;
            }
            if (endDate != null && vd.getDateHeureDepart().toLocalDate().isAfter(endDate)) {
                continue;
            }
            // Filtrer par compagnie si spécifié
            if (compagnieId != null && (vd.getVol() == null || vd.getVol().getCompagnie() == null 
                    || !vd.getVol().getCompagnie().getIdCompagnie().equals(compagnieId))) {
                continue;
            }
            // Filtrer par avion si spécifié
            if (avionId != null && (vd.getAvion() == null || !vd.getAvion().getIdAvion().equals(avionId))) {
                continue;
            }
            
            // Calcul du montant des tickets vendus (paiements confirmés)
            BigDecimal montantTickets = BigDecimal.ZERO;
            if (vd.getReservations() != null) {
                for (Reservation res : vd.getReservations()) {
                    if (res.getPaiement() != null && "PAYE".equals(res.getPaiement().getStatut())) {
                        BigDecimal montant = res.getPaiement().getMontant();
                        if (montant != null) {
                            montantTickets = montantTickets.add(montant);
                        }
                    }
                }
            }
            
            // Calcul du montant des publicités diffusées sur ce vol
            BigDecimal montantPublicites = BigDecimal.ZERO;
            BigDecimal montantPubPaye = BigDecimal.ZERO;
            List<DiffusionPublicitaire> diffusions = diffusionPublicitaireService.getByVolDetail(vd.getIdVolDetail());
            for (DiffusionPublicitaire diff : diffusions) {
                montantPublicites = montantPublicites.add(diff.getMontantTotal());
                montantPubPaye = montantPubPaye.add(diff.getMontantPaye());
            }
            
            // Calcul des produits vendus sur ce vol
            Integer nbProduitsVendus = venteProduitService.getTotalQuantiteByVolDetail(vd.getIdVolDetail());
            BigDecimal montantProduits = venteProduitService.getTotalMontantByVolDetail(vd.getIdVolDetail());
            
            // Créer le DTO
            String aeroportDepart = vd.getVol() != null && vd.getVol().getAeroportDepart() != null 
                    ? vd.getVol().getAeroportDepart().getNom() + " (" + vd.getVol().getAeroportDepart().getCodeIata() + ")"
                    : "-";
            String aeroportArrivee = vd.getVol() != null && vd.getVol().getAeroportArrivee() != null 
                    ? vd.getVol().getAeroportArrivee().getNom() + " (" + vd.getVol().getAeroportArrivee().getCodeIata() + ")"
                    : "-";
            String avion = vd.getAvion() != null ? vd.getAvion().getModele() : "-";
            
            ChiffreAffaireVolDTO dto = new ChiffreAffaireVolDTO(
                    vd.getIdVolDetail(),
                    aeroportDepart,
                    aeroportArrivee,
                    avion,
                    vd.getDateHeureDepart().toLocalDate(),
                    vd.getDateHeureDepart().toLocalTime(),
                    montantTickets,
                    montantPublicites,
                    montantPubPaye,
                    nbProduitsVendus,
                    montantProduits
            );
            
            result.add(dto);
        }
        
        return result;
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
            BigDecimal prix = reservationService.getPrixFinal(r);
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

    /**
     * CA Encaissé - affiche uniquement les montants réellement payés (tickets + publicités payées)
     */
    @GetMapping("/encaisse")
    public String encaisse(
            @RequestParam(value = "volId", required = false) Long volId,
            @RequestParam(value = "avionId", required = false) Long avionId,
            @RequestParam(value = "compagnieId", required = false) Long compagnieId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        // Calcul du CA par vol avec uniquement les montants encaissés
        List<ChiffreAffaireVolDTO> caParVol = calculerCAEncaisseParVol(startDate, endDate, compagnieId, avionId);
        
        // Totaux globaux
        BigDecimal totalTickets = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantTickets)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPublicites = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantPublicites)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Totaux produits vendus
        Integer totalNbProduits = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getNbProduitsVendus)
                .reduce(0, Integer::sum);
        BigDecimal totalMontantProduits = caParVol.stream()
                .map(ChiffreAffaireVolDTO::getMontantProduits)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalGlobal = totalTickets.add(totalPublicites).add(totalMontantProduits);

        model.addAttribute("caParVol", caParVol);
        model.addAttribute("totalTickets", totalTickets);
        model.addAttribute("totalPublicites", totalPublicites);
        model.addAttribute("totalNbProduits", totalNbProduits);
        model.addAttribute("totalMontantProduits", totalMontantProduits);
        model.addAttribute("totalGlobal", totalGlobal);
        model.addAttribute("avions", avionService.getAll());
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("selectedAvion", avionId);
        model.addAttribute("selectedCompagnie", compagnieId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "views/chiffre-affaire/encaisse";
    }

    /**
     * Calcule le CA encaissé par vol (tickets payés + publicités payées uniquement)
     */
    private List<ChiffreAffaireVolDTO> calculerCAEncaisseParVol(LocalDate startDate, LocalDate endDate, Long compagnieId, Long avionId) {
        List<ChiffreAffaireVolDTO> result = new ArrayList<>();
        List<VolDetail> volDetails = volDetailService.getAll();
        
        for (VolDetail vd : volDetails) {
            // Filtrer par date si spécifié
            if (startDate != null && vd.getDateHeureDepart().toLocalDate().isBefore(startDate)) {
                continue;
            }
            if (endDate != null && vd.getDateHeureDepart().toLocalDate().isAfter(endDate)) {
                continue;
            }
            // Filtrer par compagnie si spécifié
            if (compagnieId != null && (vd.getVol() == null || vd.getVol().getCompagnie() == null 
                    || !vd.getVol().getCompagnie().getIdCompagnie().equals(compagnieId))) {
                continue;
            }
            // Filtrer par avion si spécifié
            if (avionId != null && (vd.getAvion() == null || !vd.getAvion().getIdAvion().equals(avionId))) {
                continue;
            }
            
            // Calcul du montant des tickets vendus (paiements confirmés)
            BigDecimal montantTickets = BigDecimal.ZERO;
            if (vd.getReservations() != null) {
                for (Reservation res : vd.getReservations()) {
                    if (res.getPaiement() != null && "PAYE".equals(res.getPaiement().getStatut())) {
                        BigDecimal montant = res.getPaiement().getMontant();
                        if (montant != null) {
                            montantTickets = montantTickets.add(montant);
                        }
                    }
                }
            }
            
            // Calcul du montant des publicités DEJA PAYEES sur ce vol
            BigDecimal montantPublicites = BigDecimal.ZERO;
            List<DiffusionPublicitaire> diffusions = diffusionPublicitaireService.getByVolDetail(vd.getIdVolDetail());
            for (DiffusionPublicitaire diff : diffusions) {
                // Récupérer uniquement le montant déjà payé pour cette diffusion
                BigDecimal montantPaye = paiementPublicitaireService.getTotalPayeByDiffusion(diff.getIdDiffusion());
                if (montantPaye != null) {
                    montantPublicites = montantPublicites.add(montantPaye);
                }
            }
            
            // Calcul des produits vendus sur ce vol
            Integer nbProduitsVendus = venteProduitService.getTotalQuantiteByVolDetail(vd.getIdVolDetail());
            BigDecimal montantProduits = venteProduitService.getTotalMontantByVolDetail(vd.getIdVolDetail());
            
            // Créer le DTO
            String aeroportDepart = vd.getVol() != null && vd.getVol().getAeroportDepart() != null 
                    ? vd.getVol().getAeroportDepart().getNom() + " (" + vd.getVol().getAeroportDepart().getCodeIata() + ")"
                    : "-";
            String aeroportArrivee = vd.getVol() != null && vd.getVol().getAeroportArrivee() != null 
                    ? vd.getVol().getAeroportArrivee().getNom() + " (" + vd.getVol().getAeroportArrivee().getCodeIata() + ")"
                    : "-";
            String avion = vd.getAvion() != null ? vd.getAvion().getModele() : "-";
            
            ChiffreAffaireVolDTO dto = new ChiffreAffaireVolDTO(
                    vd.getIdVolDetail(),
                    aeroportDepart,
                    aeroportArrivee,
                    avion,
                    vd.getDateHeureDepart().toLocalDate(),
                    vd.getDateHeureDepart().toLocalTime(),
                    montantTickets,
                    montantPublicites,
                    montantPublicites,
                    nbProduitsVendus,
                    montantProduits
            );
            
            result.add(dto);
        }
        
        return result;
    }
}
