package com.aero.ops.service;

import com.aero.ops.model.Publicite;
import com.aero.ops.repository.PubliciteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PubliciteService {

    private final PubliciteRepository publiciteRepository;

    public PubliciteService(PubliciteRepository publiciteRepository) {
        this.publiciteRepository = publiciteRepository;
    }

    public List<Publicite> getAll() {
        return publiciteRepository.findAll();
    }

    public Publicite getById(Long id) {
        return publiciteRepository.findById(id).orElse(null);
    }

    public List<Publicite> getBySociete(Long idSociete) {
        return publiciteRepository.findBySocieteIdSociete(idSociete);
    }

    public Publicite create(Publicite publicite) {
        return publiciteRepository.save(publicite);
    }

    public Publicite update(Publicite publicite) {
        return publiciteRepository.save(publicite);
    }

    public void delete(Long id) {
        publiciteRepository.deleteById(id);
    }
}
