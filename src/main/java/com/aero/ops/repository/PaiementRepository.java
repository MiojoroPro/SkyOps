package com.aero.ops.repository;

import com.aero.ops.model.Vol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaiementRepository extends JpaRepository<com.aero.ops.model.Paiement, Long> {
    Optional<com.aero.ops.model.Paiement> findByReservation_IdReservation(Long idReservation);
}
