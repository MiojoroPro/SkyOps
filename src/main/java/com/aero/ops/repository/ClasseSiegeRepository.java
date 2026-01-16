package com.aero.ops.repository;

import com.aero.ops.model.ClasseSiege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClasseSiegeRepository extends JpaRepository<ClasseSiege, Long> {
    
    Optional<ClasseSiege> findByCode(String code);
}
