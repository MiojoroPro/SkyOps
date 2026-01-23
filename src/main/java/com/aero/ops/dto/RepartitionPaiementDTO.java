package com.aero.ops.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO pour le résultat de répartition d'un paiement au prorata sur une diffusion
 */
@Getter
@Setter
public class RepartitionPaiementDTO {
    
    private Long idDiffusion;
    private String publicite;
    private String vol;
    private BigDecimal montantFacture;
    private BigDecimal poidsEnPourcentage;
    private BigDecimal montantReparti;
    private BigDecimal montantDejaPayeAvant;
    private BigDecimal nouveauTotalPaye;
    private BigDecimal resteApresRepartition;
    
    public RepartitionPaiementDTO() {}
    
    public RepartitionPaiementDTO(Long idDiffusion, String publicite, String vol, 
                                   BigDecimal montantFacture, BigDecimal poidsEnPourcentage,
                                   BigDecimal montantReparti, BigDecimal montantDejaPayeAvant) {
        this.idDiffusion = idDiffusion;
        this.publicite = publicite;
        this.vol = vol;
        this.montantFacture = montantFacture;
        this.poidsEnPourcentage = poidsEnPourcentage;
        this.montantReparti = montantReparti;
        this.montantDejaPayeAvant = montantDejaPayeAvant;
        this.nouveauTotalPaye = montantDejaPayeAvant.add(montantReparti);
        this.resteApresRepartition = montantFacture.subtract(this.nouveauTotalPaye);
        
        // Ne pas avoir de reste négatif
        if (this.resteApresRepartition.compareTo(BigDecimal.ZERO) < 0) {
            this.resteApresRepartition = BigDecimal.ZERO;
        }
    }
}
