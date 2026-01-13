package com.aero.ops.service;

import com.aero.ops.model.Paiement;
import com.aero.ops.repository.PaiementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;

    public PaiementService(PaiementRepository paiementRepository) {
        this.paiementRepository = paiementRepository;
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
     * Find payments with statut=PAYE and apply optional filters: volId, userId, startDate, endDate
     */
    public java.util.List<Paiement> findPaidByFilters(Long volId, Long userId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        java.util.List<Paiement> paid = paiementRepository.findByStatut("PAYE");

        java.time.LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        java.time.LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        return paid.stream().filter(p -> {
            // Exclude payments that belong to an already cancelled reservation
            if (p.getReservation() != null && "ANNULÉ".equals(p.getReservation().getStatut())) {
                return false;
            }

            if (volId != null) {
                if (p.getReservation() == null || p.getReservation().getVolDetail() == null || p.getReservation().getVolDetail().getVol() == null
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
            if (start != null && (p.getDatePaiement() == null || p.getDatePaiement().isBefore(start))) return false;
            if (end != null && (p.getDatePaiement() == null || p.getDatePaiement().isAfter(end))) return false;
            return true;
        }).collect(java.util.stream.Collectors.toList());
    }
}
