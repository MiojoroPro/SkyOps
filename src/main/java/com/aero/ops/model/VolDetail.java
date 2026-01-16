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
    private List<VolClasse> volClasses;

    @OneToMany(mappedBy = "volDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrixClasseAge> prixClasseAges;

    @OneToMany(mappedBy = "volDetail")
    private List<Reservation> reservations;

    /**
     * Retourne les places restantes pour une classe spécifique
     */
    @Transient
    public Integer getPlacesRestantesByClasse(Long idClasse) {
        if (volClasses == null) return 0;
        return volClasses.stream()
                .filter(vc -> vc.getClasseSiege() != null && vc.getClasseSiege().getIdClasse().equals(idClasse))
                .findFirst()
                .map(VolClasse::getPlacesRestantes)
                .orElse(0);
    }

    /**
     * Retourne le prix pour une classe et une catégorie d'âge spécifiques
     */
    @Transient
    public BigDecimal getPrix(Long idClasse, Long idCategorie) {
        if (prixClasseAges == null) return BigDecimal.ZERO;
        return prixClasseAges.stream()
                .filter(p -> p.getClasseSiege() != null && p.getClasseSiege().getIdClasse().equals(idClasse)
                        && p.getCategorieAge() != null && p.getCategorieAge().getIdCategorie().equals(idCategorie))
                .findFirst()
                .map(PrixClasseAge::getPrix)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Calcule la recette maximale possible pour ce vol (toutes classes, prix max par classe)
     */
    @Transient
    public BigDecimal getMaxRevenue() {
        if (volClasses == null || prixClasseAges == null) return BigDecimal.ZERO;
        
        BigDecimal total = BigDecimal.ZERO;
        for (VolClasse vc : volClasses) {
            if (vc.getClasseSiege() == null || avion == null) continue;
            
            // Capacité de la classe pour cet avion
            Integer capacite = avion.getCapaciteByClasse(vc.getClasseSiege().getIdClasse());
            
            // Prix max pour cette classe (parmi toutes les catégories d'âge)
            BigDecimal prixMax = prixClasseAges.stream()
                    .filter(p -> p.getClasseSiege() != null 
                            && p.getClasseSiege().getIdClasse().equals(vc.getClasseSiege().getIdClasse()))
                    .map(PrixClasseAge::getPrix)
                    .filter(p -> p != null)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
            
            total = total.add(prixMax.multiply(BigDecimal.valueOf(capacite)));
        }
        return total;
    }

    /**
     * Retourne le total des places restantes (toutes classes confondues)
     */
    @Transient
    public int getTotalPlacesRestantes() {
        if (volClasses == null) return 0;
        return volClasses.stream()
                .mapToInt(vc -> vc.getPlacesRestantes() != null ? vc.getPlacesRestantes() : 0)
                .sum();
    }

    /**
     * Calcule le chiffre d'affaires reel genere (basé sur les reservations effectives)
     * Somme des prix des reservations CONFIRMEE ou EN_ATTENTE
     */
    @Transient
    public BigDecimal getChiffreAffairesReel() {
        if (reservations == null || reservations.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal total = BigDecimal.ZERO;
        for (Reservation res : reservations) {
            if (res.getStatut() != null && 
                (res.getStatut().equals("CONFIRMEE") || res.getStatut().equals("EN_ATTENTE"))) {
                // Recuperer le prix depuis prix_classe_age
                BigDecimal prix = getPrix(
                    res.getClasseSiege() != null ? res.getClasseSiege().getIdClasse() : null,
                    res.getCategorieAge() != null ? res.getCategorieAge().getIdCategorie() : null
                );
                total = total.add(prix);
            }
        }
        return total;
    }

    /**
     * Retourne le nombre de places reservees (CONFIRMEE + EN_ATTENTE)
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
