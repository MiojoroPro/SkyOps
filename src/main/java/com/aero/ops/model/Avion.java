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
    private int capacite;
    private String statut;

    @ManyToOne
    @JoinColumn(name = "id_compagnie")
    private Compagnie compagnie;
}
