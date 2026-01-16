package com.aero.ops.repository;

import com.aero.ops.model.AvionClasse;
import com.aero.ops.model.AvionClasseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvionClasseRepository extends JpaRepository<AvionClasse, AvionClasseId> {
    
    List<AvionClasse> findByAvion_IdAvion(Long idAvion);
    
    void deleteByAvion_IdAvion(Long idAvion);
}
