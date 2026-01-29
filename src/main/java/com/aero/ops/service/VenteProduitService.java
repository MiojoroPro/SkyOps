package com.aero.ops.service;

import com.aero.ops.model.VenteProduit;
import com.aero.ops.repository.VenteProduitRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class VenteProduitService {

    private final VenteProduitRepository venteProduitRepository;

    public VenteProduitService(VenteProduitRepository venteProduitRepository) {
        this.venteProduitRepository = venteProduitRepository;
    }

    public List<VenteProduit> getAll() {
        return venteProduitRepository.findAll();
    }

    public Optional<VenteProduit> getById(Long id) {
        return venteProduitRepository.findById(id);
    }

    public List<VenteProduit> getByVolDetail(Long idVolDetail) {
        return venteProduitRepository.findByVolDetailIdVolDetail(idVolDetail);
    }

    public List<VenteProduit> getByProduit(Long idProduit) {
        return venteProduitRepository.findByProduitIdProduit(idProduit);
    }

    public Integer getTotalQuantiteByVolDetail(Long idVolDetail) {
        return venteProduitRepository.getTotalQuantiteByVolDetail(idVolDetail);
    }

    public BigDecimal getTotalMontantByVolDetail(Long idVolDetail) {
        return venteProduitRepository.getTotalMontantByVolDetail(idVolDetail);
    }

    public Integer getTotalQuantiteByCompagnie(Long idCompagnie) {
        return venteProduitRepository.getTotalQuantiteByCompagnie(idCompagnie);
    }

    public BigDecimal getTotalMontantByCompagnie(Long idCompagnie) {
        return venteProduitRepository.getTotalMontantByCompagnie(idCompagnie);
    }

    public VenteProduit save(VenteProduit vente) {
        return venteProduitRepository.save(vente);
    }

    public void delete(Long id) {
        venteProduitRepository.deleteById(id);
    }
}
