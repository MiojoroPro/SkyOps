package com.aero.ops.model;

import java.io.Serializable;
import java.util.Objects;

public class PrixClasseAgeId implements Serializable {
    
    private Long volDetail;
    private Long classeSiege;
    private Long categorieAge;

    public PrixClasseAgeId() {}

    public PrixClasseAgeId(Long volDetail, Long classeSiege, Long categorieAge) {
        this.volDetail = volDetail;
        this.classeSiege = classeSiege;
        this.categorieAge = categorieAge;
    }

    public Long getVolDetail() {
        return volDetail;
    }

    public void setVolDetail(Long volDetail) {
        this.volDetail = volDetail;
    }

    public Long getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(Long classeSiege) {
        this.classeSiege = classeSiege;
    }

    public Long getCategorieAge() {
        return categorieAge;
    }

    public void setCategorieAge(Long categorieAge) {
        this.categorieAge = categorieAge;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrixClasseAgeId that = (PrixClasseAgeId) o;
        return Objects.equals(volDetail, that.volDetail) 
            && Objects.equals(classeSiege, that.classeSiege) 
            && Objects.equals(categorieAge, that.categorieAge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volDetail, classeSiege, categorieAge);
    }
}
