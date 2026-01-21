package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "remise_classe_categorie")
@IdClass(RemiseClasseCategorieId.class)
@Getter
@Setter
public class RemiseClasseCategorie {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_categorie")
    private CategorieAge categorieAge;

    /**
     * Pourcentage de réduction pour cette classe et catégorie
     * Entre 0 et 100 (valeur en pourcentage)
     */
    @Column(name = "pourcentage", nullable = false)
    private BigDecimal pourcentage = BigDecimal.ZERO;

    /**
     * Calcule le prix final avec la remise appliquée
     */
    public BigDecimal appliquerRemise(BigDecimal prixBase) {
        if (prixBase == null || pourcentage == null) {
            return prixBase;
        }
        return prixBase.multiply(BigDecimal.ONE.subtract(pourcentage.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
    }
}
