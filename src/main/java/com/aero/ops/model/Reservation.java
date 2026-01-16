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

    @Column(name = "numero_reservation", unique = true)
    private String numeroReservation;

    @Column(name = "statut")
    private String statut;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_vol_detail")
    private VolDetail volDetail;

    @ManyToOne
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @ManyToOne
    @JoinColumn(name = "id_categorie")
    private CategorieAge categorieAge;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Paiement paiement;
}
