package com.aero.ops.model;

import java.io.Serializable;
import java.util.Objects;

public class VolClasseId implements Serializable {
    
    private Long volDetail;
    private Long classeSiege;

    public VolClasseId() {}

    public VolClasseId(Long volDetail, Long classeSiege) {
        this.volDetail = volDetail;
        this.classeSiege = classeSiege;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolClasseId that = (VolClasseId) o;
        return Objects.equals(volDetail, that.volDetail) && Objects.equals(classeSiege, that.classeSiege);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volDetail, classeSiege);
    }
}
