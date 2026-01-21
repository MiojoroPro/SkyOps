package com.aero.ops.dto;

import com.aero.ops.model.ClasseSiege;
import com.aero.ops.model.CategorieAge;
import java.math.BigDecimal;

/**
 * DTO pour afficher le prix d'une classe-catégorie pour un vol
 */
public class PrixVolDTO {
    private ClasseSiege classeSiege;
    private CategorieAge categorieAge;
    private BigDecimal prix;

    public PrixVolDTO(ClasseSiege classeSiege, CategorieAge categorieAge, BigDecimal prix) {
        this.classeSiege = classeSiege;
        this.categorieAge = categorieAge;
        this.prix = prix;
    }

    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }

    public CategorieAge getCategorieAge() {
        return categorieAge;
    }

    public void setCategorieAge(CategorieAge categorieAge) {
        this.categorieAge = categorieAge;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
}
