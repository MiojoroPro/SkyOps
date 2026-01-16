package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "avion")
@Getter
@Setter
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avion")
    private Long idAvion;

    @Column(name = "modele")
    private String modele;

    @Column(name = "statut")
    private String statut;

    @ManyToOne
    @JoinColumn(name = "id_compagnie")
    private Compagnie compagnie;

    @OneToMany(mappedBy = "avion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvionClasse> avionClasses;

    @OneToMany(mappedBy = "avion")
    private List<VolDetail> volDetails;

    /**
     * Calcule la capacité totale de l'avion (somme de toutes les classes)
     */
    @Transient
    public int getCapaciteTotale() {
        if (avionClasses == null || avionClasses.isEmpty()) {
            return 0;
        }
        return avionClasses.stream()
                .mapToInt(ac -> ac.getCapacite() != null ? ac.getCapacite() : 0)
                .sum();
    }

    /**
     * Retourne la capacité pour une classe spécifique
     */
    @Transient
    public Integer getCapaciteByClasse(Long idClasse) {
        if (avionClasses == null) return 0;
        return avionClasses.stream()
                .filter(ac -> ac.getClasseSiege() != null && ac.getClasseSiege().getIdClasse().equals(idClasse))
                .findFirst()
                .map(AvionClasse::getCapacite)
                .orElse(0);
    }
}
