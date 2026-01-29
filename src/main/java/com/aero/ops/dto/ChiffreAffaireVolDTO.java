package com.aero.ops.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiffreAffaireVolDTO {
    
    private Long idVolDetail;
    private String aeroportDepart;
    private String aeroportArrivee;
    private String avion;
    private LocalDate dateDepart;
    private LocalTime heureDepart;
    private BigDecimal montantTickets;
    private BigDecimal montantPublicites;      // Montant total des publicités
    private BigDecimal montantPubPaye;         // Montant publicités payé
    private BigDecimal montantPubRestant;      // Montant publicités restant à payer
    private Integer nbProduitsVendus;          // Nombre de produits vendus
    private BigDecimal montantProduits;        // Montant des ventes de produits
    private BigDecimal montantTotal;           // Total CA encaissé (tickets + pub payé + produits)
    private BigDecimal caPotentiel;            // Total CA si tout est payé (tickets + pub total + produits)
    
    /**
     * Constructeur avec calcul automatique du total
     */
    public ChiffreAffaireVolDTO(Long idVolDetail, String aeroportDepart, String aeroportArrivee, 
                                 String avion, LocalDate dateDepart, LocalTime heureDepart,
                                 BigDecimal montantTickets, BigDecimal montantPublicites,
                                 BigDecimal montantPubPaye, Integer nbProduitsVendus, 
                                 BigDecimal montantProduits) {
        this.idVolDetail = idVolDetail;
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.avion = avion;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.montantTickets = montantTickets != null ? montantTickets : BigDecimal.ZERO;
        this.montantPublicites = montantPublicites != null ? montantPublicites : BigDecimal.ZERO;
        this.montantPubPaye = montantPubPaye != null ? montantPubPaye : BigDecimal.ZERO;
        this.montantPubRestant = this.montantPublicites.subtract(this.montantPubPaye);
        this.nbProduitsVendus = nbProduitsVendus != null ? nbProduitsVendus : 0;
        this.montantProduits = montantProduits != null ? montantProduits : BigDecimal.ZERO;
        // Total CA encaissé = tickets + publicités payées + produits vendus
        this.montantTotal = this.montantTickets.add(this.montantPubPaye).add(this.montantProduits);
        // CA potentiel = tickets + toutes les publicités + produits
        this.caPotentiel = this.montantTickets.add(this.montantPublicites).add(this.montantProduits);
    }
}
