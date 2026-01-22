package com.aero.ops.repository;

import com.aero.ops.model.SocieteAnnonceur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocieteAnnonceurRepository extends JpaRepository<SocieteAnnonceur, Long> {
}
