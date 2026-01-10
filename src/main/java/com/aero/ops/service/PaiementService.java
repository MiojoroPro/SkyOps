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
}
