package com.aero.ops.dto;

import com.aero.ops.model.ClasseSiege;

/**
 * DTO pour afficher une classe de siège avec les places restantes pour un vol
 */
public class ClasseVolDTO {
    private ClasseSiege classeSiege;
    private Integer placesRestantes;

    public ClasseVolDTO(ClasseSiege classeSiege, Integer placesRestantes) {
        this.classeSiege = classeSiege;
        this.placesRestantes = placesRestantes;
    }

    public ClasseSiege getClasseSiege() {
        return classeSiege;
    }

    public void setClasseSiege(ClasseSiege classeSiege) {
        this.classeSiege = classeSiege;
    }

    public Integer getPlacesRestantes() {
        return placesRestantes;
    }

    public void setPlacesRestantes(Integer placesRestantes) {
        this.placesRestantes = placesRestantes;
    }
}
