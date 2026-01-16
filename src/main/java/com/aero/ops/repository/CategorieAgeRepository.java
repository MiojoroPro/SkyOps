package com.aero.ops.repository;

import com.aero.ops.model.CategorieAge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorieAgeRepository extends JpaRepository<CategorieAge, Long> {
    
    @Query("SELECT c FROM CategorieAge c WHERE :age >= c.ageMin AND :age <= c.ageMax")
    Optional<CategorieAge> findByAge(@Param("age") int age);
}
