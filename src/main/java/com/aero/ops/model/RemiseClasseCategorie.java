package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "remise_classe_categorie")
@Getter
@Setter
public class RemiseClasseCategorie {

    @EmbeddedId
    private RemiseClasseCategorieId id;

    @ManyToOne
    @MapsId("idClasse")
    @JoinColumn(name = "id_classe")
    private ClasseSiege classeSiege;

    @ManyToOne
    @MapsId("idCategorie")
    @JoinColumn(name = "id_categorie")
    private CategorieAge categorieAge;

    /**
     * Pourcentage du tarif adulte pour cette classe et catégorie
     * Ex: 100 pour adulte, 75 pour enfant en éco, 60 pour enfant en première
     */
    @Column(name = "pourcentage", nullable = false)
    private BigDecimal pourcentage = BigDecimal.valueOf(100);

    /**
     * Calcule le prix pour cette combinaison classe/catégorie à partir du prix adulte
     */
    public BigDecimal calculerPrix(BigDecimal prixAdulte) {
        if (prixAdulte == null || pourcentage == null) {
            return BigDecimal.ZERO;
        }
        return prixAdulte.multiply(pourcentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
