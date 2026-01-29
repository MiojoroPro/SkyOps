package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vente_produit")
@Getter
@Setter
public class VenteProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vente")
    private Long idVente;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire_vente", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaireVente;

    @Column(name = "date_vente", nullable = false)
    private LocalDateTime dateVente = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_produit", nullable = false)
    private ProduitExtra produit;

    @ManyToOne
    @JoinColumn(name = "id_vol_detail", nullable = false)
    private VolDetail volDetail;

    /**
     * Calcule le montant total de cette vente
     */
    @Transient
    public BigDecimal getMontantTotal() {
        if (prixUnitaireVente == null || quantite == null) {
            return BigDecimal.ZERO;
        }
        return prixUnitaireVente.multiply(BigDecimal.valueOf(quantite));
    }
}
