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

    @Column(name = "numero_vol")
    private String numeroVol;

    @ManyToOne
    @JoinColumn(name = "id_compagnie")
    private Compagnie compagnie;

    @ManyToOne
    @JoinColumn(name = "id_aeroport_depart")
    private Aeroport aeroportDepart;

    @ManyToOne
    @JoinColumn(name = "id_aeroport_arrivee")
    private Aeroport aeroportArrivee;

    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL)
    private List<VolDetail> volDetails;
}
