package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "classe_siege")
@Getter
@Setter
public class ClasseSiege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_classe")
    private Long idClasse;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "libelle")
    private String libelle;

    @OneToMany(mappedBy = "classeSiege")
    private List<AvionClasse> avionClasses;

    @OneToMany(mappedBy = "classeSiege")
    private List<VolClasse> volClasses;

    @OneToMany(mappedBy = "classeSiege")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "classeSiege")
    private List<RemiseClasseCategorie> remises;
}
