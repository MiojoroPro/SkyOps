package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "vol")
@Getter
@Setter
public class Vol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vol")
    private Long idVol;

    @Column(name = "numero_vol", nullable = false)
    private String numeroVol;

    @Column(name = "prix_base", nullable = false)
    private double prixBase;

    @Column(name = "prix_economique")
    private Double prixEconomique;

    @Column(name = "prix_premiere")
    private Double prixPremiere;

    @Column(name = "prix_premium")
    private Double prixPremium;

    @ManyToOne
    @JoinColumn(name = "id_compagnie", nullable = false)
    private Compagnie compagnie;

    @ManyToOne
    @JoinColumn(name = "id_aeroport_depart", nullable = false)
    private Aeroport aeroportDepart;

    @ManyToOne
    @JoinColumn(name = "id_aeroport_arrivee", nullable = false)
    private Aeroport aeroportArrivee;

    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<VolDetail> volDetails;
}
