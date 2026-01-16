package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "vol_classe")
@IdClass(VolClasseId.class)
@Getter
@Setter
public class VolClasse {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_vol_detail")
    private VolDetail volDetail;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @Column(name = "places_restantes")
    private Integer placesRestantes;

    @OneToMany(mappedBy = "volClasse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrixClasseAge> prixClasseAges;
}
