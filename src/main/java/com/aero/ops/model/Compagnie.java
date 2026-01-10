package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "compagnie")
@Getter
@Setter
public class Compagnie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compagnie")
    private Long idCompagnie;

    private String nom;

    @Column(name = "code_iata")
    private String codeIata;

    @Column(name = "code_icao")
    private String codeIcao;

    private String pays;
}
