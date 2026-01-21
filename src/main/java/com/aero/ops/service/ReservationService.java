package com.aero.ops.service;

import com.aero.ops.model.*;
import com.aero.ops.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VolDetailService volDetailService;

    public ReservationService(ReservationRepository reservationRepository,
                              VolDetailService volDetailService) {
        this.reservationRepository = reservationRepository;
        this.volDetailService = volDetailService;
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getByUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateur_IdUtilisateur(utilisateurId);
    }

    public List<Reservation> getByVol(Long volId) {
        return reservationRepository.findByVolDetail_Vol_IdVol(volId);
    }

    public Reservation getById(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    /**
     * Recherche les réservations avec filtres multiples.
     */
    public List<Reservation> findByFilters(Long volId, Long userId, Long avionId, Long compagnieId, 
                                           String statut, LocalDate startDate, LocalDate endDate) {
        List<Reservation> all = reservationRepository.findAll();
        
        return all.stream()
                .filter(r -> volId == null || (r.getVolDetail() != null && r.getVolDetail().getVol() != null 
                        && r.getVolDetail().getVol().getIdVol().equals(volId)))
                .filter(r -> userId == null || (r.getUtilisateur() != null 
                        && r.getUtilisateur().getIdUtilisateur().equals(userId)))
                .filter(r -> avionId == null || (r.getVolDetail() != null && r.getVolDetail().getAvion() != null 
                        && r.getVolDetail().getAvion().getIdAvion().equals(avionId)))
                .filter(r -> compagnieId == null || (r.getVolDetail() != null && r.getVolDetail().getVol() != null 
                        && r.getVolDetail().getVol().getCompagnie() != null 
                        && r.getVolDetail().getVol().getCompagnie().getIdCompagnie().equals(compagnieId)))
                .filter(r -> statut == null || statut.isEmpty() || statut.equals(r.getStatut()))
                .filter(r -> startDate == null || (r.getVolDetail() != null && r.getVolDetail().getDateHeureDepart() != null 
                        && !r.getVolDetail().getDateHeureDepart().toLocalDate().isBefore(startDate)))
                .filter(r -> endDate == null || (r.getVolDetail() != null && r.getVolDetail().getDateHeureDepart() != null 
                        && !r.getVolDetail().getDateHeureDepart().toLocalDate().isAfter(endDate)))
                .collect(Collectors.toList());
    }

    /**
     * Crée une nouvelle réservation avec vérification des places.
     * 
     * Règles métier:
     * 1. Vérifier que des places sont disponibles pour la classe choisie
     * 2. Les places restantes sont calculées dynamiquement: Capacité - Nombre de réservations
     * 3. Le prix final est calculé à partir du prix de base avec remise appliquée
     */
    @Transactional
    public Reservation create(Reservation reservation) {
        validateReservation(reservation);
        
        if (reservation.getVolDetail() == null || reservation.getVolDetail().getIdVolDetail() == null) {
            throw new IllegalArgumentException("Le vol est obligatoire.");
        }
        
        if (reservation.getClasseSiege() == null || reservation.getClasseSiege().getIdClasse() == null) {
            throw new IllegalArgumentException("La classe de siège est obligatoire.");
        }

        // Vérifier qu'il y a des places disponibles
        VolDetail volDetail = reservation.getVolDetail();
        Long idClasse = reservation.getClasseSiege().getIdClasse();
        
        int placesRestantes = volDetail.getPlacesRestantesByClasse(idClasse);
        if (placesRestantes <= 0) {
            throw new IllegalStateException("Aucune place disponible pour la classe " 
                    + reservation.getClasseSiege().getLibelle() + " sur ce vol.");
        }

        // Générer numéro de réservation
        if (reservation.getNumeroReservation() == null || reservation.getNumeroReservation().isEmpty()) {
            reservation.setNumeroReservation(generateNumeroReservation());
        }

        if (reservation.getStatut() == null) {
            reservation.setStatut("EN_ATTENTE");
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Génère un numéro de réservation unique
     */
    private String generateNumeroReservation() {
        return "RES-" + System.currentTimeMillis();
    }

    /**
     * Met à jour une réservation.
     * Si la réservation est annulée, les places sont libérées (recalculées dynamiquement).
     */
    @Transactional
    public Reservation update(Reservation reservation) {
        Reservation existing = reservationRepository.findById(reservation.getIdReservation()).orElse(null);
        
        if (existing == null) {
            throw new IllegalArgumentException("Réservation non trouvée.");
        }
        
        // Validation des changements
        validateReservation(reservation);
        
        return reservationRepository.save(reservation);
    }

    /**
     * Annule une réservation (les places sont libérées automatiquement via recalcul dynamique)
     */
    @Transactional
    public Reservation cancel(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        if (reservation == null) {
            throw new IllegalArgumentException("Réservation non trouvée.");
        }
        return cancel(reservation);
    }
    
    /**
     * Annule une réservation (les places sont libérées automatiquement via recalcul dynamique)
     */
    @Transactional
    public Reservation cancel(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Réservation non trouvée.");
        }
        
        if ("ANNULÉ".equals(reservation.getStatut())) {
            return reservation; // Déjà annulée
        }
        
        reservation.setStatut("ANNULÉ");
        return reservationRepository.save(reservation);
    }

    /**
     * Calcule le prix final pour une réservation (prix de base + remise appliquée)
     */
    public BigDecimal getPrixFinal(Reservation reservation) {
        if (reservation.getVolDetail() == null 
                || reservation.getClasseSiege() == null 
                || reservation.getCategorieAge() == null) {
            return BigDecimal.ZERO;
        }
        
        return volDetailService.getPrixFinal(
                reservation.getVolDetail().getIdVolDetail(),
                reservation.getClasseSiege().getIdClasse(),
                reservation.getCategorieAge().getIdCategorie()
        );
    }

    /**
     * Récupère le prix de base (sans remise)
     */
    public BigDecimal getPrixBase(Reservation reservation) {
        if (reservation.getVolDetail() == null || reservation.getClasseSiege() == null) {
            return BigDecimal.ZERO;
        }
        
        return volDetailService.getPrixBase(
                reservation.getVolDetail().getIdVolDetail(),
                reservation.getClasseSiege().getIdClasse()
        );
    }

    /**
     * Vérifie la disponibilité des places pour une classe donnée.
     * Les places sont calculées comme: Capacité - Nombre de réservations confirmées/en attente
     */
    public boolean hasPlacesDisponibles(Long idVolDetail, Long idClasse) {
        int placesRestantes = volDetailService.getPlacesRestantes(idVolDetail, idClasse);
        return placesRestantes > 0;
    }

    /**
     * Retourne le nombre de places restantes pour une classe donnée.
     * Calculé comme: Capacité - Nombre de réservations confirmées/en attente
     */
    public int getPlacesRestantes(Long idVolDetail, Long idClasse) {
        return volDetailService.getPlacesRestantes(idVolDetail, idClasse);
    }

    /**
     * Retourne le nombre total de places réservées pour un vol
     */
    public int getNombrePlacesReservees(Long idVolDetail) {
        VolDetail volDetail = volDetailService.getById(idVolDetail);
        if (volDetail == null) return 0;
        return volDetail.getNombrePlacesReservees();
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    /**
     * Valide que les champs obligatoires sont remplis et que les places sont disponibles
     */
    private void validateReservation(Reservation reservation) {
        if (reservation.getVolDetail() == null) {
            throw new IllegalArgumentException("Le vol est obligatoire.");
        }
        if (reservation.getClasseSiege() == null) {
            throw new IllegalArgumentException("La classe de siège est obligatoire.");
        }
        if (reservation.getCategorieAge() == null) {
            throw new IllegalArgumentException("La catégorie d'âge est obligatoire.");
        }
        if (reservation.getUtilisateur() == null) {
            throw new IllegalArgumentException("L'utilisateur est obligatoire.");
        }
        
        // Vérifier les places disponibles
        Long idVolDetail = reservation.getVolDetail().getIdVolDetail();
        Long idClasse = reservation.getClasseSiege().getIdClasse();
        
        if (!hasPlacesDisponibles(idVolDetail, idClasse)) {
            throw new IllegalStateException("Aucune place disponible pour la classe " 
                    + reservation.getClasseSiege().getLibelle() + " sur ce vol.");
        }
    }
}
