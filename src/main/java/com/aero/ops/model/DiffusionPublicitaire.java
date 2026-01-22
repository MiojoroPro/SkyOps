package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "diffusion_publicitaire")
@Getter
@Setter
public class DiffusionPublicitaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diffusion")
    private Long idDiffusion;

    @Column(name = "mois", nullable = false)
    private Integer mois;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "nombre_diffusions", nullable = false)
    private Integer nombreDiffusions;

    @ManyToOne
    @JoinColumn(name = "id_publicite", nullable = false)
    private Publicite publicite;

    @ManyToOne
    @JoinColumn(name = "id_tarif", nullable = false)
    private TarifPublicitaire tarif;

    @ManyToOne
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;

    /**
     * Calcul du montant total pour cette diffusion
     */
    public BigDecimal getMontantTotal() {
        if (tarif != null && tarif.getPrixUnitaire() != null && nombreDiffusions != null) {
            return tarif.getPrixUnitaire().multiply(BigDecimal.valueOf(nombreDiffusions));
        }
        return BigDecimal.ZERO;
    }
}
