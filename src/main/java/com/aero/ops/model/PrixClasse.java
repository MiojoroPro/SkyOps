package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "prix_classe")
@IdClass(PrixClasseId.class)
@Getter
@Setter
public class PrixClasse {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_vol_detail")
    private VolDetail volDetail;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @Column(name = "prix_base", nullable = false)
    private BigDecimal prixBase;
}
