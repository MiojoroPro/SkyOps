package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "id_vol")
    private Vol vol;

    @ManyToOne
    @JoinColumn(name = "id_avion")
    private Avion avion;

    @OneToMany(mappedBy = "volDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrixClasse> prixClasses;

    @OneToMany(mappedBy = "volDetail")
    private List<Reservation> reservations;

    /**
     * Calcule les places restantes pour une classe spécifique basées sur les réservations
     * Places restantes = Capacité totale - Nombre de réservations (confirmées + en attente)
     */
    @Transient
    public Integer getPlacesRestantesByClasse(Long idClasse) {
        if (avion == null) return 0;
        
        // Capacité de la classe pour cet avion
        Integer capacite = avion.getCapaciteByClasse(idClasse);
        
        // Nombre de réservations confirmées/en attente pour cette classe
        long reservations_count = reservations == null ? 0 : reservations.stream()
                .filter(r -> r.getClasseSiege() != null 
                        && r.getClasseSiege().getIdClasse().equals(idClasse)
                        && r.getStatut() != null
                        && (r.getStatut().equals("CONFIRMEE") || r.getStatut().equals("EN_ATTENTE")))
                .count();
        
        return (int) (capacite - reservations_count);
    }

    /**
     * Retourne le total des places restantes pour TOUTES les classes
     */
    @Transient
    public Integer getPlacesTotalesRestantes() {
        if (avion == null) return 0;
        
        int totalPlaces = 0;
        List<AvionClasse> avionClasses = avion.getAvionClasses();
        
        if (avionClasses != null) {
            for (AvionClasse ac : avionClasses) {
                Integer placesRestantes = getPlacesRestantesByClasse(ac.getClasseSiege().getIdClasse());
                if (placesRestantes != null) {
                    totalPlaces += placesRestantes;
                }
            }
        }
        
        return totalPlaces;
    }

    /**
     * Retourne le prix de base pour une classe dans ce vol
     */
    @Transient
    public BigDecimal getPrixBase(Long idClasse) {
        if (prixClasses == null) return BigDecimal.ZERO;
        return prixClasses.stream()
                .filter(p -> p.getClasseSiege() != null && p.getClasseSiege().getIdClasse().equals(idClasse))
                .findFirst()
                .map(PrixClasse::getPrixBase)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calcule le prix final pour une classe et catégorie d'âge (avec remise appliquée)
     */
    @Transient
    public BigDecimal getPrixFinal(Long idClasse, Long idCategorie) {
        BigDecimal prixBase = getPrixBase(idClasse);
        if (prixBase.equals(BigDecimal.ZERO)) return BigDecimal.ZERO;
        
        // Chercher la remise pour cette classe et catégorie
        RemiseClasseCategorie remise = getRemiseForClasseAndCategorie(idClasse, idCategorie);
        
        if (remise != null && remise.getPourcentage() != null) {
            BigDecimal pourcentageRemise = remise.getPourcentage().divide(BigDecimal.valueOf(100));
            return prixBase.multiply(BigDecimal.ONE.subtract(pourcentageRemise));
        }
        
        return prixBase;
    }

    /**
     * Récupère la remise pour une classe et catégorie
     */
    @Transient
    private RemiseClasseCategorie getRemiseForClasseAndCategorie(Long idClasse, Long idCategorie) {
        // Cette méthode fait appel au repository de RemiseClasseCategorie
        // Implémentation dans le service
        return null;
    }

    /**
     * Calcule la recette maximale possible pour ce vol (toutes classes, prix max par classe)
     */
    @Transient
    public BigDecimal getMaxRevenue() {
        if (prixClasses == null || avion == null) return BigDecimal.ZERO;
        
        BigDecimal total = BigDecimal.ZERO;
        for (PrixClasse pc : prixClasses) {
            if (pc.getClasseSiege() == null) continue;
            
            // Capacité de la classe pour cet avion
            Integer capacite = avion.getCapaciteByClasse(pc.getClasseSiege().getIdClasse());
            
            // Prix de base pour cette classe
            BigDecimal prixBase = pc.getPrixBase();
            
            total = total.add(prixBase.multiply(BigDecimal.valueOf(capacite)));
        }
        return total;
    }

    /**
     * Retourne le total des places restantes (toutes classes confondues)
     */
    @Transient
    public int getTotalPlacesRestantes() {
        if (avion == null || avion.getAvionClasses() == null) return 0;
        
        int total = 0;
        for (AvionClasse ac : avion.getAvionClasses()) {
            if (ac.getClasseSiege() != null) {
                total += getPlacesRestantesByClasse(ac.getClasseSiege().getIdClasse());
            }
        }
        return total;
    }

    /**
     * Calcule le chiffre d'affaires réel généré (basé sur les réservations effectives)
     * Somme des prix (avec remises appliquées) des réservations CONFIRMEE ou EN_ATTENTE
     */
    @Transient
    public BigDecimal getChiffreAffairesReel() {
        if (reservations == null || reservations.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal total = BigDecimal.ZERO;
        for (Reservation res : reservations) {
            if (res.getStatut() != null && 
                (res.getStatut().equals("CONFIRMEE") || res.getStatut().equals("EN_ATTENTE"))) {
                // Récupérer le prix avec remise appliquée
                BigDecimal prix = getPrixFinal(
                    res.getClasseSiege() != null ? res.getClasseSiege().getIdClasse() : null,
                    res.getCategorieAge() != null ? res.getCategorieAge().getIdCategorie() : null
                );
                total = total.add(prix);
            }
        }
        return total;
    }

    /**
     * Retourne le nombre de places réservées (CONFIRMEE + EN_ATTENTE)
     */
    @Transient
    public int getNombrePlacesReservees() {
        if (reservations == null) return 0;
        return (int) reservations.stream()
                .filter(r -> r.getStatut() != null && 
                        (r.getStatut().equals("CONFIRMEE") || r.getStatut().equals("EN_ATTENTE")))
                .count();
    }
}
