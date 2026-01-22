package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "publicite")
@Getter
@Setter
public class Publicite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicite")
    private Long idPublicite;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "duree_seconde", nullable = false)
    private Integer dureeSeconde;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_societe", nullable = false)
    private SocieteAnnonceur societe;
}
