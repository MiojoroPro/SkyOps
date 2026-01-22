package com.aero.ops.repository;

import com.aero.ops.model.Publicite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PubliciteRepository extends JpaRepository<Publicite, Long> {
    List<Publicite> findBySocieteIdSociete(Long idSociete);
}
