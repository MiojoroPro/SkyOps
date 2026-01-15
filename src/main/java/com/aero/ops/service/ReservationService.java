package com.aero.ops.service;

import com.aero.ops.model.Reservation;
import com.aero.ops.model.VolDetail;
import com.aero.ops.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VolDetailService volDetailService;

    public ReservationService(ReservationRepository reservationRepository, VolDetailService volDetailService) {
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

    @Transactional
    public Reservation create(Reservation reservation) {
        reservation.setDateReservation(LocalDateTime.now());
        if (reservation.getStatut() == null) {
            reservation.setStatut("EN_ATTENTE");
        }

        // Adjust remaining seats based on chosen class
        VolDetail detail = reservation.getVolDetail();
        if (detail != null && reservation.getClasse() != null) {
            if ("ECONOMIQUE".equalsIgnoreCase(reservation.getClasse())) {
                if (detail.getPlacesEcoRestantes() == null || detail.getPlacesEcoRestantes() <= 0) {
                    throw new IllegalStateException("Aucune place économique disponible pour ce vol.");
                }
                detail.setPlacesEcoRestantes(detail.getPlacesEcoRestantes() - 1);
            } else if ("PREMIERE".equalsIgnoreCase(reservation.getClasse())) {
                if (detail.getPlacesPremiereRestantes() == null || detail.getPlacesPremiereRestantes() <= 0) {
                    throw new IllegalStateException("Aucune place première disponible pour ce vol.");
                }
                detail.setPlacesPremiereRestantes(detail.getPlacesPremiereRestantes() - 1);
            } else if ("PREMIUM".equalsIgnoreCase(reservation.getClasse())) {
                if (detail.getPlacesPremiumRestantes() == null || detail.getPlacesPremiumRestantes() <= 0) {
                    throw new IllegalStateException("Aucune place premium disponible pour ce vol.");
                }
                detail.setPlacesPremiumRestantes(detail.getPlacesPremiumRestantes() - 1);
            }
            volDetailService.update(detail);
        }

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation update(Reservation reservation) {
        // If reservation is being cancelled, restore seat
        Reservation existing = reservationRepository.findById(reservation.getIdReservation()).orElse(null);
        if (existing != null && !"ANNULÉ".equals(existing.getStatut()) && "ANNULÉ".equals(reservation.getStatut())) {
            VolDetail detail = reservation.getVolDetail();
            if (detail != null && reservation.getClasse() != null) {
                if ("ECONOMIQUE".equalsIgnoreCase(reservation.getClasse())) {
                    detail.setPlacesEcoRestantes((detail.getPlacesEcoRestantes() == null ? 0 : detail.getPlacesEcoRestantes()) + 1);
                } else if ("PREMIERE".equalsIgnoreCase(reservation.getClasse())) {
                    detail.setPlacesPremiereRestantes((detail.getPlacesPremiereRestantes() == null ? 0 : detail.getPlacesPremiereRestantes()) + 1);
                } else if ("PREMIUM".equalsIgnoreCase(reservation.getClasse())) {
                    detail.setPlacesPremiumRestantes((detail.getPlacesPremiumRestantes() == null ? 0 : detail.getPlacesPremiumRestantes()) + 1);
                }
                volDetailService.update(detail);
            }
        }
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}
