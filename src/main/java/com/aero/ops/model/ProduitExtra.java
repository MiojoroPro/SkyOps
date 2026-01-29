package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "produit_extra")
@Getter
@Setter
public class ProduitExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(nullable = false)
    private Integer stock = 0;

    @ManyToOne
    @JoinColumn(name = "id_compagnie", nullable = false)
    private Compagnie compagnie;

    /**
     * Vérifie si le stock est suffisant pour une quantité donnée
     */
    public boolean hasStock(int quantite) {
        return stock != null && stock >= quantite;
    }

    /**
     * Décrémente le stock
     */
    public void decrementerStock(int quantite) {
        if (stock != null) {
            this.stock -= quantite;
        }
    }

    /**
     * Incrémente le stock (réapprovisionnement ou annulation vente)
     */
    public void incrementerStock(int quantite) {
        if (stock == null) {
            this.stock = quantite;
        } else {
            this.stock += quantite;
        }
    }
}
