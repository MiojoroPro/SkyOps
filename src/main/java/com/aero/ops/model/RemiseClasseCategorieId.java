package com.aero.ops.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RemiseClasseCategorieId implements Serializable {

    @Column(name = "id_classe")
    private Long idClasse;

    @Column(name = "id_categorie")
    private Long idCategorie;
}
