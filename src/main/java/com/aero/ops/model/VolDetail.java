package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vol_detail")
@Getter
@Setter
public class VolDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vol_detail")
    private Long idVolDetail;

    @Column(name = "date_heure_depart", nullable = false)
    private LocalDateTime dateHeureDepart;

    @Column(name = "date_heure_arrivee", nullable = false)
    private LocalDateTime dateHeureArrivee;

    @Column(name = "statut")
    private String statut;

    @Column(name = "prix_economique")
    private Double prixEconomique;

    @Column(name = "prix_premiere")
    private Double prixPremiere;

    @Column(name = "prix_premium")
    private Double prixPremium;

    @Column(name = "places_eco_restantes")
    private Integer placesEcoRestantes;

    @Column(name = "places_premiere_restantes")
    private Integer placesPremiereRestantes;

    @Column(name = "places_premium_restantes")
    private Integer placesPremiumRestantes;

    @ManyToOne
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;

    @Transient
    public Double getMaxRevenue() {
        int capEco = (avion != null) ? avion.getCapaciteEconomique() : 0;
        int capPrem = (avion != null) ? avion.getCapacitePremiere() : 0;
        int capPremium = (avion != null) ? avion.getCapacitePremium() : 0;
        double pEco = (prixEconomique != null) ? prixEconomique : (vol != null ? (vol.getPrixEconomique() != null ? vol.getPrixEconomique() : vol.getPrixBase()) : 0.0);
        double pPrem = (prixPremiere != null) ? prixPremiere : (vol != null ? (vol.getPrixPremiere() != null ? vol.getPrixPremiere() : vol.getPrixBase() * 1.5) : 0.0);
        double pPremium = (prixPremium != null) ? prixPremium : (vol != null ? (vol.getPrixPremium() != null ? vol.getPrixPremium() : vol.getPrixBase() * 1.25) : 0.0);
        return capEco * pEco + capPrem * pPrem + capPremium * pPremium;
    }
}
