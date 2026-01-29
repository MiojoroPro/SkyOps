package com.aero.ops.repository;

import com.aero.ops.model.VenteProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VenteProduitRepository extends JpaRepository<VenteProduit, Long> {
    
    List<VenteProduit> findByVolDetailIdVolDetail(Long idVolDetail);
    
    List<VenteProduit> findByProduitIdProduit(Long idProduit);
    
    @Query("SELECT COALESCE(SUM(v.quantite), 0) FROM VenteProduit v WHERE v.volDetail.idVolDetail = :idVolDetail")
    Integer getTotalQuantiteByVolDetail(@Param("idVolDetail") Long idVolDetail);
    
    @Query("SELECT COALESCE(SUM(v.quantite * v.prixUnitaireVente), 0) FROM VenteProduit v WHERE v.volDetail.idVolDetail = :idVolDetail")
    BigDecimal getTotalMontantByVolDetail(@Param("idVolDetail") Long idVolDetail);
    
    @Query("SELECT COALESCE(SUM(v.quantite), 0) FROM VenteProduit v WHERE v.produit.compagnie.idCompagnie = :idCompagnie")
    Integer getTotalQuantiteByCompagnie(@Param("idCompagnie") Long idCompagnie);
    
    @Query("SELECT COALESCE(SUM(v.quantite * v.prixUnitaireVente), 0) FROM VenteProduit v WHERE v.produit.compagnie.idCompagnie = :idCompagnie")
    BigDecimal getTotalMontantByCompagnie(@Param("idCompagnie") Long idCompagnie);
}
