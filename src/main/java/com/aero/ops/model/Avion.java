package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "avion")
@Getter
@Setter
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avion")
    private Long idAvion;

    private String modele;

    @Column(name = "capacite_economique")
    private int capaciteEconomique;

    @Column(name = "capacite_premiere")
    private int capacitePremiere;

    @Column(name = "capacite_premium")
    private int capacitePremium;

    // Computed total - optional, not stored in DB (database has generated column)
    public int getCapaciteTotale() {
        return capaciteEconomique + capacitePremiere + capacitePremium;
    }

    private String statut;

    @ManyToOne
    @JoinColumn(name = "id_compagnie")
    private Compagnie compagnie;
}
