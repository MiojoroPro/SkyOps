package com.aero.ops.service;

import com.aero.ops.model.ProduitExtra;
import com.aero.ops.repository.ProduitExtraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitExtraService {

    private final ProduitExtraRepository produitExtraRepository;

    public ProduitExtraService(ProduitExtraRepository produitExtraRepository) {
        this.produitExtraRepository = produitExtraRepository;
    }

    public List<ProduitExtra> getAll() {
        return produitExtraRepository.findAll();
    }

    public Optional<ProduitExtra> getById(Long id) {
        return produitExtraRepository.findById(id);
    }

    public List<ProduitExtra> getByCompagnie(Long idCompagnie) {
        return produitExtraRepository.findByCompagnieIdCompagnie(idCompagnie);
    }

    public ProduitExtra save(ProduitExtra produit) {
        return produitExtraRepository.save(produit);
    }

    public void delete(Long id) {
        produitExtraRepository.deleteById(id);
    }
}
