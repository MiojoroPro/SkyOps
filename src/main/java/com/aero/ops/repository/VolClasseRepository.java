package com.aero.ops.repository;

import com.aero.ops.model.VolClasse;
import com.aero.ops.model.VolClasseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolClasseRepository extends JpaRepository<VolClasse, VolClasseId> {
    
    List<VolClasse> findByVolDetail_IdVolDetail(Long idVolDetail);
    
    Optional<VolClasse> findByVolDetail_IdVolDetailAndClasseSiege_IdClasse(Long idVolDetail, Long idClasse);
    
    void deleteByVolDetail_IdVolDetail(Long idVolDetail);
    
    @Modifying
    @Query("UPDATE VolClasse vc SET vc.placesRestantes = vc.placesRestantes - 1 " +
           "WHERE vc.volDetail.idVolDetail = :idVolDetail AND vc.classeSiege.idClasse = :idClasse " +
           "AND vc.placesRestantes > 0")
    int decrementPlacesRestantes(@Param("idVolDetail") Long idVolDetail, @Param("idClasse") Long idClasse);
    
    @Modifying
    @Query("UPDATE VolClasse vc SET vc.placesRestantes = vc.placesRestantes + 1 " +
           "WHERE vc.volDetail.idVolDetail = :idVolDetail AND vc.classeSiege.idClasse = :idClasse")
    int incrementPlacesRestantes(@Param("idVolDetail") Long idVolDetail, @Param("idClasse") Long idClasse);
}
