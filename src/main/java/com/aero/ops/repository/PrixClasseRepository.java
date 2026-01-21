package com.aero.ops.repository;

import com.aero.ops.model.PrixClasse;
import com.aero.ops.model.PrixClasseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrixClasseRepository extends JpaRepository<PrixClasse, PrixClasseId> {
    
    List<PrixClasse> findByVolDetail_IdVolDetail(Long idVolDetail);
    
    Optional<PrixClasse> findByVolDetail_IdVolDetailAndClasseSiege_IdClasse(Long idVolDetail, Long idClasse);
}
