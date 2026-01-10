package com.aero.ops.repository;

import com.aero.ops.model.Utilisateur;
import com.aero.ops.model.Vol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<com.aero.ops.model.Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
}