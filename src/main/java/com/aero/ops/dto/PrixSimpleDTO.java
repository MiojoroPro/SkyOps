package com.aero.ops.dto;

import java.math.BigDecimal;

/**
 * DTO simplifié pour la sérialisation JavaScript (évite les références circulaires)
 */
public class PrixSimpleDTO {
    private Long classeId;
    private Long categorieId;
    private BigDecimal prix;

    public PrixSimpleDTO(Long classeId, Long categorieId, BigDecimal prix) {
        this.classeId = classeId;
        this.categorieId = categorieId;
        this.prix = prix;
    }

    public Long getClasseId() {
        return classeId;
    }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public Long getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
}
