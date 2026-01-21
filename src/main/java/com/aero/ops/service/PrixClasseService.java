package com.aero.ops.service;

import com.aero.ops.model.PrixClasse;
import com.aero.ops.repository.PrixClasseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PrixClasseService {

    private final PrixClasseRepository prixClasseRepository;

    public PrixClasseService(PrixClasseRepository prixClasseRepository) {
        this.prixClasseRepository = prixClasseRepository;
    }

    public List<PrixClasse> getByVolDetail(Long idVolDetail) {
        return prixClasseRepository.findByVolDetail_IdVolDetail(idVolDetail);
    }

    public Optional<PrixClasse> getByVolDetailAndClasse(Long idVolDetail, Long idClasse) {
        return prixClasseRepository.findByVolDetail_IdVolDetailAndClasseSiege_IdClasse(idVolDetail, idClasse);
    }

    public BigDecimal getPrixBase(Long idVolDetail, Long idClasse) {
        return getByVolDetailAndClasse(idVolDetail, idClasse)
                .map(PrixClasse::getPrixBase)
                .orElse(BigDecimal.ZERO);
    }

    public PrixClasse save(PrixClasse prixClasse) {
        return prixClasseRepository.save(prixClasse);
    }

    @Transactional
    public void deleteByVolDetail(Long idVolDetail) {
        List<PrixClasse> prixClasses = getByVolDetail(idVolDetail);
        prixClasseRepository.deleteAll(prixClasses);
    }
}
