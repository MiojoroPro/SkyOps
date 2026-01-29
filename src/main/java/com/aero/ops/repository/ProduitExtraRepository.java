package com.aero.ops.repository;

import com.aero.ops.model.ProduitExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitExtraRepository extends JpaRepository<ProduitExtra, Long> {
    
    List<ProduitExtra> findByCompagnieIdCompagnie(Long idCompagnie);
}
