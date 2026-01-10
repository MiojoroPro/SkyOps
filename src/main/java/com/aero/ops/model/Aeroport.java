package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "aeroport")
@Getter
@Setter
public class Aeroport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aeroport")
    private Long idAeroport;

    private String nom;
    private String ville;
    private String pays;

    @Column(name = "code_iata")
    private String codeIata;
}
