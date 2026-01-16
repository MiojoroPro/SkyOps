package com.aero.ops.service;

import com.aero.ops.model.*;
import com.aero.ops.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VolClasseService volClasseService;
    private final PrixClasseAgeService prixClasseAgeService;

    public ReservationService(ReservationRepository reservationRepository,
                              VolClasseService volClasseService,
                              PrixClasseAgeService prixClasseAgeService) {
        this.reservationRepository = reservationRepository;
        this.volClasseService = volClasseService;
        this.prixClasseAgeService = prixClasseAgeService;
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
     * Crée une nouvelle réservation avec vérification et décrémentation des places.
     * 
     * Règles métier:
     * 1. Vérifier que places_restantes > 0 pour la classe choisie
     * 2. Décrémenter les places après validation
     * 3. Le prix est récupéré depuis prix_classe_age
     */
    @Transactional
    public Reservation create(Reservation reservation) {
        validateReservation(reservation);
        
        reservation.setDateReservation(LocalDateTime.now());
        if (reservation.getStatut() == null) {
            reservation.setStatut("EN_ATTENTE");
        }

        Long idVolDetail = reservation.getVolDetail().getIdVolDetail();
        Long idClasse = reservation.getClasseSiege().getIdClasse();

        // Vérifier et décrémenter les places
        boolean decremented = volClasseService.decrementPlaces(idVolDetail, idClasse);
        if (!decremented) {
            throw new IllegalStateException("Aucune place disponible pour la classe " 
                    + reservation.getClasseSiege().getLibelle() + " sur ce vol.");
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Met à jour une réservation.
     * Si la réservation est annulée, restaure les places.
     */
    @Transactional
    public Reservation update(Reservation reservation) {
        Reservation existing = reservationRepository.findById(reservation.getIdReservation()).orElse(null);
        
        // Si la réservation passe à ANNULÉ, restaurer les places
        if (existing != null 
                && !"ANNULÉ".equals(existing.getStatut()) 
                && "ANNULÉ".equals(reservation.getStatut())) {
            
            if (reservation.getVolDetail() != null && reservation.getClasseSiege() != null) {
                volClasseService.incrementPlaces(
                        reservation.getVolDetail().getIdVolDetail(),
                        reservation.getClasseSiege().getIdClasse()
                );
            }
        }
        
        return reservationRepository.save(reservation);
    }

    /**
     * Annule une réservation et restaure les places.
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
     * Annule une réservation et restaure les places.
     */
    @Transactional
    public Reservation cancel(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Réservation non trouvée.");
        }
        
        if ("ANNULÉ".equals(reservation.getStatut())) {
            return reservation; // Déjà annulée
        }
        
        // Restaurer les places
        if (reservation.getVolDetail() != null && reservation.getClasseSiege() != null) {
            volClasseService.incrementPlaces(
                    reservation.getVolDetail().getIdVolDetail(),
                    reservation.getClasseSiege().getIdClasse()
            );
        }
        
        reservation.setStatut("ANNULÉ");
        return reservationRepository.save(reservation);
    }

    /**
     * Récupère le prix depuis prix_classe_age.
     */
    public BigDecimal getPrix(Reservation reservation) {
        if (reservation.getVolDetail() == null 
                || reservation.getClasseSiege() == null 
                || reservation.getCategorieAge() == null) {
            return BigDecimal.ZERO;
        }
        
        return prixClasseAgeService.getMontant(
                reservation.getVolDetail().getIdVolDetail(),
                reservation.getClasseSiege().getIdClasse(),
                reservation.getCategorieAge().getIdCategorie()
        );
    }

    /**
     * Vérifie la disponibilité des places pour une classe donnée.
     */
    public boolean hasPlacesDisponibles(Long idVolDetail, Long idClasse) {
        return volClasseService.hasPlacesDisponibles(idVolDetail, idClasse);
    }

    /**
     * Retourne le nombre de places restantes pour une classe donnée.
     */
    public int getPlacesRestantes(Long idVolDetail, Long idClasse) {
        return volClasseService.getPlacesRestantes(idVolDetail, idClasse);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

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
        
        if (!volClasseService.hasPlacesDisponibles(idVolDetail, idClasse)) {
            throw new IllegalStateException("Aucune place disponible pour la classe " 
                    + reservation.getClasseSiege().getLibelle() + " sur ce vol.");
        }
    }
}
