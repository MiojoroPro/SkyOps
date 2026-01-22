package com.aero.ops.repository;

import com.aero.ops.model.TarifPublicitaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TarifPublicitaireRepository extends JpaRepository<TarifPublicitaire, Long> {
    
    /**
     * Trouve le tarif applicable à une date donnée
     */
    @Query("SELECT t FROM TarifPublicitaire t WHERE t.dateDebut <= :date AND (t.dateFin IS NULL OR t.dateFin >= :date)")
    Optional<TarifPublicitaire> findTarifApplicable(@Param("date") LocalDate date);
}
