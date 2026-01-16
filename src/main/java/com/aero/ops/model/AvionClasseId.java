package com.aero.ops.model;

import java.io.Serializable;
import java.util.Objects;

public class AvionClasseId implements Serializable {
    
    private Long avion;
    private Long classeSiege;

    public AvionClasseId() {}

    public AvionClasseId(Long avion, Long classeSiege) {
        this.avion = avion;
        this.classeSiege = classeSiege;
    }

    public Long getAvion() {
        return avion;
    }

    public void setAvion(Long avion) {
        this.avion = avion;
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
        AvionClasseId that = (AvionClasseId) o;
        return Objects.equals(avion, that.avion) && Objects.equals(classeSiege, that.classeSiege);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avion, classeSiege);
    }
}
