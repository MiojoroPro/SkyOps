package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "prix_classe_age")
@IdClass(PrixClasseAgeId.class)
@Getter
@Setter
public class PrixClasseAge {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_vol_detail")
    private VolDetail volDetail;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_categorie")
    private CategorieAge categorieAge;

    @Column(name = "prix")
    private BigDecimal prix;

    // Convenience reference to VolClasse (for navigating through relations)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "id_vol_detail", referencedColumnName = "id_vol_detail", insertable = false, updatable = false),
        @JoinColumn(name = "id_classe", referencedColumnName = "id_classe", insertable = false, updatable = false)
    })
    private VolClasse volClasse;
}
