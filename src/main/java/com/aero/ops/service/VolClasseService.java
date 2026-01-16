package com.aero.ops.service;

import com.aero.ops.model.VolClasse;
import com.aero.ops.model.VolClasseId;
import com.aero.ops.repository.VolClasseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VolClasseService {

    private final VolClasseRepository volClasseRepository;

    public VolClasseService(VolClasseRepository volClasseRepository) {
        this.volClasseRepository = volClasseRepository;
    }

    public List<VolClasse> getByVolDetail(Long idVolDetail) {
        return volClasseRepository.findByVolDetail_IdVolDetail(idVolDetail);
    }

    public Optional<VolClasse> getByVolDetailAndClasse(Long idVolDetail, Long idClasse) {
        return volClasseRepository.findByVolDetail_IdVolDetailAndClasseSiege_IdClasse(idVolDetail, idClasse);
    }

    public VolClasse save(VolClasse volClasse) {
        return volClasseRepository.save(volClasse);
    }

    @Transactional
    public void deleteByVolDetail(Long idVolDetail) {
        volClasseRepository.deleteByVolDetail_IdVolDetail(idVolDetail);
    }

    /**
     * Décrémente les places restantes pour une classe donnée.
     * @return true si la décrémentation a réussi (places_restantes > 0), false sinon
     */
    @Transactional
    public boolean decrementPlaces(Long idVolDetail, Long idClasse) {
        int updated = volClasseRepository.decrementPlacesRestantes(idVolDetail, idClasse);
        return updated > 0;
    }

    /**
     * Incrémente les places restantes pour une classe donnée (annulation).
     */
    @Transactional
    public void incrementPlaces(Long idVolDetail, Long idClasse) {
        volClasseRepository.incrementPlacesRestantes(idVolDetail, idClasse);
    }

    /**
     * Vérifie si des places sont disponibles pour une classe donnée.
     */
    public boolean hasPlacesDisponibles(Long idVolDetail, Long idClasse) {
        return getByVolDetailAndClasse(idVolDetail, idClasse)
                .map(vc -> vc.getPlacesRestantes() != null && vc.getPlacesRestantes() > 0)
                .orElse(false);
    }

    /**
     * Retourne le nombre de places restantes pour une classe donnée.
     */
    public int getPlacesRestantes(Long idVolDetail, Long idClasse) {
        return getByVolDetailAndClasse(idVolDetail, idClasse)
                .map(vc -> vc.getPlacesRestantes() != null ? vc.getPlacesRestantes() : 0)
                .orElse(0);
    }
}
