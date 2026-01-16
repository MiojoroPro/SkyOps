package com.aero.ops.service;

import com.aero.ops.model.Paiement;
import com.aero.ops.model.Reservation;
import com.aero.ops.repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final ReservationService reservationService;

    public PaiementService(PaiementRepository paiementRepository, ReservationService reservationService) {
        this.paiementRepository = paiementRepository;
        this.reservationService = reservationService;
    }

    public List<Paiement> getAll() {
        return paiementRepository.findAll();
    }

    public Paiement getById(Long id) {
        return paiementRepository.findById(id).orElse(null);
    }

    public Optional<Paiement> getByReservation(Long reservationId) {
        return paiementRepository.findByReservation_IdReservation(reservationId);
    }

    /**
     * Crée un paiement pour une réservation.
     * Le montant est récupéré automatiquement depuis prix_classe_age.
     */
    @Transactional
    public Paiement createForReservation(Reservation reservation, boolean payerMaintenant) {
        BigDecimal montant = reservationService.getPrix(reservation);
        
        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(montant);
        
        if (payerMaintenant) {
            paiement.setStatut("PAYE");
            paiement.setDatePaiement(LocalDateTime.now());
        } else {
            paiement.setStatut("NON_PAYE");
        }
        
        return paiementRepository.save(paiement);
    }

    /**
     * Effectue le paiement d'une réservation.
     */
    @Transactional
    public Paiement pay(Long reservationId) {
        Reservation reservation = reservationService.getById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Réservation non trouvée.");
        }
        
        if ("ANNULÉ".equals(reservation.getStatut())) {
            throw new IllegalStateException("Impossible de payer une réservation annulée.");
        }
        
        Optional<Paiement> existingOpt = paiementRepository.findByReservation_IdReservation(reservationId);
        
        Paiement paiement;
        if (existingOpt.isPresent()) {
            paiement = existingOpt.get();
        } else {
            paiement = new Paiement();
            paiement.setReservation(reservation);
        }
        
        // Récupérer le montant depuis prix_classe_age
        BigDecimal montant = reservationService.getPrix(reservation);
        paiement.setMontant(montant);
        paiement.setStatut("PAYE");
        paiement.setDatePaiement(LocalDateTime.now());
        
        // Mettre à jour le statut de la réservation
        reservation.setStatut("CONFIRMEE");
        reservationService.update(reservation);
        
        return paiementRepository.save(paiement);
    }

    public Paiement create(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public Paiement update(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public void delete(Long id) {
        paiementRepository.deleteById(id);
    }

    /**
     * Find payments with statut=PAYE and apply optional filters: volId, userId, avionId, compagnieId, startDate, endDate
     */
    public List<Paiement> findPaidByFilters(Long volId, Long userId, Long avionId, Long compagnieId, LocalDate startDate, LocalDate endDate) {
        List<Paiement> paid = paiementRepository.findByStatut("PAYE");

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        return paid.stream().filter(p -> {
            // Exclude payments that belong to an already cancelled reservation
            if (p.getReservation() != null && "ANNULE".equals(p.getReservation().getStatut())) {
                return false;
            }

            if (volId != null) {
                if (p.getReservation() == null || p.getReservation().getVolDetail() == null 
                        || p.getReservation().getVolDetail().getVol() == null
                        || p.getReservation().getVolDetail().getVol().getIdVol() == null
                        || !p.getReservation().getVolDetail().getVol().getIdVol().equals(volId)) {
                    return false;
                }
            }
            if (userId != null) {
                if (p.getReservation() == null || p.getReservation().getUtilisateur() == null
                        || p.getReservation().getUtilisateur().getIdUtilisateur() == null
                        || !p.getReservation().getUtilisateur().getIdUtilisateur().equals(userId)) {
                    return false;
                }
            }
            // Filtre par avion
            if (avionId != null) {
                if (p.getReservation() == null || p.getReservation().getVolDetail() == null 
                        || p.getReservation().getVolDetail().getAvion() == null
                        || p.getReservation().getVolDetail().getAvion().getIdAvion() == null
                        || !p.getReservation().getVolDetail().getAvion().getIdAvion().equals(avionId)) {
                    return false;
                }
            }
            // Filtre par compagnie
            if (compagnieId != null) {
                if (p.getReservation() == null || p.getReservation().getVolDetail() == null 
                        || p.getReservation().getVolDetail().getVol() == null
                        || p.getReservation().getVolDetail().getVol().getCompagnie() == null
                        || p.getReservation().getVolDetail().getVol().getCompagnie().getIdCompagnie() == null
                        || !p.getReservation().getVolDetail().getVol().getCompagnie().getIdCompagnie().equals(compagnieId)) {
                    return false;
                }
            }
            if (start != null && (p.getDatePaiement() == null || p.getDatePaiement().isBefore(start))) return false;
            if (end != null && (p.getDatePaiement() == null || p.getDatePaiement().isAfter(end))) return false;
            return true;
        }).collect(Collectors.toList());
    }
}
