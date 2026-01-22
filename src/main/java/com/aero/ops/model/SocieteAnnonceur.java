package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "societe_annonceur")
@Getter
@Setter
public class SocieteAnnonceur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_societe")
    private Long idSociete;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;
}
