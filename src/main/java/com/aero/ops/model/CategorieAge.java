package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categorie_age")
@Getter
@Setter
public class CategorieAge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categorie")
    private Long idCategorie;

    @Column(name = "libelle")
    private String libelle;

    @Column(name = "age_min", nullable = false)
    private Integer ageMin;

    @Column(name = "age_max", nullable = false)
    private Integer ageMax;

    @OneToMany(mappedBy = "categorieAge")
    private List<PrixClasseAge> prixClasseAges;

    @OneToMany(mappedBy = "categorieAge")
    private List<Reservation> reservations;
}
