package com.aero.ops.repository;

import com.aero.ops.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUtilisateur_IdUtilisateur(Long idUtilisateur);
    List<Reservation> findByVolDetail_Vol_IdVol(Long idVol);
}
