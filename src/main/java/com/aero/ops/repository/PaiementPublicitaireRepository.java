package com.aero.ops.repository;

import com.aero.ops.model.PaiementPublicitaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaiementPublicitaireRepository extends JpaRepository<PaiementPublicitaire, Long> {
    
    /**
     * Liste les paiements d'une diffusion
     */
    List<PaiementPublicitaire> findByDiffusionIdDiffusion(Long idDiffusion);
    
    /**
     * Calcule le total payé pour une diffusion
     */
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PaiementPublicitaire p WHERE p.diffusion.idDiffusion = :idDiffusion")
    BigDecimal getTotalPayeByDiffusion(@Param("idDiffusion") Long idDiffusion);
}
