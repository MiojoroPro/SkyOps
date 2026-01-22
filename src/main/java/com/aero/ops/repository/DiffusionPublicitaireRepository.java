package com.aero.ops.repository;

import com.aero.ops.model.DiffusionPublicitaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiffusionPublicitaireRepository extends JpaRepository<DiffusionPublicitaire, Long> {
    
    /**
     * Trouve toutes les diffusions pour un mois et une année donnés
     */
    List<DiffusionPublicitaire> findByMoisAndAnnee(Integer mois, Integer annee);
    
    /**
     * Trouve toutes les diffusions pour une société donnée
     */
    @Query("SELECT d FROM DiffusionPublicitaire d WHERE d.publicite.societe.idSociete = :idSociete")
    List<DiffusionPublicitaire> findBySociete(@Param("idSociete") Long idSociete);
    
    /**
     * Trouve toutes les diffusions pour une société, un mois et une année donnés
     */
    @Query("SELECT d FROM DiffusionPublicitaire d WHERE d.publicite.societe.idSociete = :idSociete AND d.mois = :mois AND d.annee = :annee")
    List<DiffusionPublicitaire> findBySocieteAndMoisAndAnnee(@Param("idSociete") Long idSociete, @Param("mois") Integer mois, @Param("annee") Integer annee);
    
    /**
     * Trouve toutes les diffusions pour une année donnée
     */
    List<DiffusionPublicitaire> findByAnnee(Integer annee);
    
    /**
     * Trouve toutes les diffusions pour un avion donné
     */
    List<DiffusionPublicitaire> findByAvionIdAvion(Long idAvion);
    
    /**
     * Trouve toutes les diffusions pour un avion, un mois et une année donnés
     */
    List<DiffusionPublicitaire> findByAvionIdAvionAndMoisAndAnnee(Long idAvion, Integer mois, Integer annee);
}
