package com.aero.ops.controller;

import com.aero.ops.model.Paiement;
import com.aero.ops.service.PaiementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    public List<Paiement> getAll() {
        return paiementService.getAll();
    }

    @GetMapping("/{id}")
    public Paiement getById(@PathVariable Long id) {
        return paiementService.getById(id);
    }

    @PostMapping
    public Paiement create(@RequestBody Paiement paiement) {
        return paiementService.create(paiement);
    }

    @PutMapping("/{id}")
    public Paiement update(@PathVariable Long id, @RequestBody Paiement paiement) {
        paiement.setIdPaiement(id);
        return paiementService.update(paiement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        paiementService.delete(id);
    }
}
