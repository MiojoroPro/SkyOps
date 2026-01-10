package com.aero.ops.repository;

import com.aero.ops.model.Vol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompagnieRepository extends JpaRepository<com.aero.ops.model.Compagnie, Long> {}
