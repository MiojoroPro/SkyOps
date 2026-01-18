package com.aero.ops.repository;

import com.aero.ops.model.RemiseClasseCategorie;
import com.aero.ops.model.RemiseClasseCategorieId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemiseClasseCategorieRepository extends JpaRepository<RemiseClasseCategorie, RemiseClasseCategorieId> {

    List<RemiseClasseCategorie> findByClasseSiege_IdClasse(Long idClasse);

    List<RemiseClasseCategorie> findByCategorieAge_IdCategorie(Long idCategorie);

    @Query("SELECT r FROM RemiseClasseCategorie r WHERE r.classeSiege.idClasse = :idClasse AND r.categorieAge.idCategorie = :idCategorie")
    Optional<RemiseClasseCategorie> findByClasseAndCategorie(@Param("idClasse") Long idClasse, @Param("idCategorie") Long idCategorie);

    @Query("SELECT r FROM RemiseClasseCategorie r ORDER BY r.classeSiege.idClasse, r.categorieAge.idCategorie")
    List<RemiseClasseCategorie> findAllOrderByClasseAndCategorie();
}
