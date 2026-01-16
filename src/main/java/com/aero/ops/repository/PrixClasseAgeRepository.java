package com.aero.ops.repository;

import com.aero.ops.model.PrixClasseAge;
import com.aero.ops.model.PrixClasseAgeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrixClasseAgeRepository extends JpaRepository<PrixClasseAge, PrixClasseAgeId> {
    
    List<PrixClasseAge> findByVolDetail_IdVolDetail(Long idVolDetail);
    
    List<PrixClasseAge> findByVolDetail_IdVolDetailAndClasseSiege_IdClasse(Long idVolDetail, Long idClasse);
    
    Optional<PrixClasseAge> findByVolDetail_IdVolDetailAndClasseSiege_IdClasseAndCategorieAge_IdCategorie(
            Long idVolDetail, Long idClasse, Long idCategorie);
    
    void deleteByVolDetail_IdVolDetail(Long idVolDetail);
}
