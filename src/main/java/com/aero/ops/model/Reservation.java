package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long idReservation;

    @Column(name = "date_reservation")
    private LocalDateTime dateReservation;

    @Column(name = "numero_reservation")
    private String numeroReservation;

    @Column(name = "classe")
    private String classe;

    private String statut;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_vol_detail")
    private VolDetail volDetail;

    @OneToOne(mappedBy = "reservation")
    private Paiement paiement;
}
