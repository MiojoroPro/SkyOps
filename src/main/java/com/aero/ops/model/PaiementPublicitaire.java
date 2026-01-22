package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiement_publicitaire")
@Getter
@Setter
public class PaiementPublicitaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement_pub")
    private Long idPaiementPub;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement;

    @Column(name = "reference")
    private String reference;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @ManyToOne
    @JoinColumn(name = "id_diffusion", nullable = false)
    private DiffusionPublicitaire diffusion;
}
