package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "id_vol_detail", nullable = false)
    private VolDetail volDetail;

    @OneToMany(mappedBy = "diffusion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaiementPublicitaire> paiements = new ArrayList<>();

    /**
     * Calcul du montant total pour cette diffusion
     */
    public BigDecimal getMontantTotal() {
        if (tarif != null && tarif.getPrixUnitaire() != null && nombreDiffusions != null) {
            return tarif.getPrixUnitaire().multiply(BigDecimal.valueOf(nombreDiffusions));
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcul du montant total payé
     */
    public BigDecimal getMontantPaye() {
        if (paiements == null || paiements.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return paiements.stream()
                .map(PaiementPublicitaire::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcul du reste à payer
     */
    public BigDecimal getResteAPayer() {
        return getMontantTotal().subtract(getMontantPaye());
    }

    /**
     * Vérifie si la diffusion est entièrement payée
     */
    public boolean isPayeComplet() {
        return getResteAPayer().compareTo(BigDecimal.ZERO) <= 0;
    }

    /**
     * Retourne le statut de paiement
     */
    public String getStatutPaiement() {
        BigDecimal reste = getResteAPayer();
        if (reste.compareTo(BigDecimal.ZERO) <= 0) {
            return "PAYE";
        } else if (getMontantPaye().compareTo(BigDecimal.ZERO) > 0) {
            return "PARTIEL";
        }
        return "NON_PAYE";
    }
}
