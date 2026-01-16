package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "avion_classe")
@IdClass(AvionClasseId.class)
@Getter
@Setter
public class AvionClasse {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_avion")
    private Avion avion;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @Column(name = "capacite")
    private Integer capacite;
}
