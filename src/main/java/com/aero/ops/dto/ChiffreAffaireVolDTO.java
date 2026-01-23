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
    private BigDecimal montantTotal;           // Total CA encaissé (tickets + pub payé)
    private BigDecimal caPotentiel;            // Total CA si tout est payé (tickets + pub total)
    
    /**
     * Constructeur avec calcul automatique du total
     */
    public ChiffreAffaireVolDTO(Long idVolDetail, String aeroportDepart, String aeroportArrivee, 
                                 String avion, LocalDate dateDepart, LocalTime heureDepart,
                                 BigDecimal montantTickets, BigDecimal montantPublicites,
                                 BigDecimal montantPubPaye) {
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
        // Total CA encaissé = tickets + publicités payées uniquement
        this.montantTotal = this.montantTickets.add(this.montantPubPaye);
        // CA potentiel = tickets + toutes les publicités
        this.caPotentiel = this.montantTickets.add(this.montantPublicites);
    }
}
