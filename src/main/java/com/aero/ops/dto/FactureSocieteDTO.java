package com.aero.ops.dto;

import com.aero.ops.model.DiffusionPublicitaire;
import com.aero.ops.model.SocieteAnnonceur;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO représentant une facture groupée par société avec ses factures filles (diffusions)
 */
@Getter
@Setter
public class FactureSocieteDTO {
    
    private SocieteAnnonceur societe;
    private List<DiffusionPublicitaire> diffusions = new ArrayList<>();
    private BigDecimal montantTotal = BigDecimal.ZERO;
    private BigDecimal montantPaye = BigDecimal.ZERO;
    private BigDecimal resteAPayer = BigDecimal.ZERO;
    
    public FactureSocieteDTO(SocieteAnnonceur societe) {
        this.societe = societe;
    }
    
    /**
     * Ajoute une diffusion à la facture et met à jour les totaux
     */
    public void addDiffusion(DiffusionPublicitaire diffusion) {
        this.diffusions.add(diffusion);
        this.montantTotal = this.montantTotal.add(diffusion.getMontantTotal());
        this.montantPaye = this.montantPaye.add(diffusion.getMontantPaye());
        this.resteAPayer = this.resteAPayer.add(diffusion.getResteAPayer());
    }
    
    /**
     * Calcule le pourcentage payé
     */
    public BigDecimal getPourcentagePaye() {
        if (montantTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montantPaye.multiply(BigDecimal.valueOf(100))
                .divide(montantTotal, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Retourne le statut de paiement global
     */
    public String getStatutPaiement() {
        if (resteAPayer.compareTo(BigDecimal.ZERO) <= 0) {
            return "PAYE";
        } else if (montantPaye.compareTo(BigDecimal.ZERO) > 0) {
            return "PARTIEL";
        }
        return "NON_PAYE";
    }
    
    /**
     * Calcule le poids (pourcentage) d'une diffusion par rapport au total
     */
    public BigDecimal getPoidsFacture(DiffusionPublicitaire diffusion) {
        if (montantTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return diffusion.getMontantTotal()
                .divide(montantTotal, 6, RoundingMode.HALF_UP);
    }
}
