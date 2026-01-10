package com.aero.ops.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    private String nom;
    private String prenom;
    private String email;

    @Column(name = "mot_de_passe")
    private String motDePasse;

    private String role; // exemple : "ROLE_ADMIN" ou "ROLE_USER"

    @OneToMany(mappedBy = "utilisateur")
    private List<Reservation> reservations;

    // ================= UserDetails =================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> role); // simple rôle
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email; // on se connecte avec l'email
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // toujours actif
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // jamais verrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // jamais expiré
    }

    @Override
    public boolean isEnabled() {
        return true; // toujours activé
    }
}
