package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
@Getter
@Setter
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Long idPaiement;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Column(name = "montant")
    private BigDecimal montant;

    @Column(name = "statut")
    private String statut;

    @OneToOne
    @JoinColumn(name = "id_reservation", unique = true)
    private Reservation reservation;
}
