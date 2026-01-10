package com.aero.ops.service;

import com.aero.ops.model.Reservation;
import com.aero.ops.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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

    public Reservation create(Reservation reservation) {
        reservation.setDateReservation(LocalDateTime.now());
        if (reservation.getStatut() == null) {
            reservation.setStatut("EN_ATTENTE");
        }
        return reservationRepository.save(reservation);
    }

    public Reservation update(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}
