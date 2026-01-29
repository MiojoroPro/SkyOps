package com.aero.ops.controller;

import com.aero.ops.model.ProduitExtra;
import com.aero.ops.model.VenteProduit;
import com.aero.ops.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/produits-extra")
public class ProduitExtraController {

    private final ProduitExtraService produitExtraService;
    private final VenteProduitService venteProduitService;
    private final CompagnieService compagnieService;
    private final VolDetailService volDetailService;

    public ProduitExtraController(ProduitExtraService produitExtraService,
                                  VenteProduitService venteProduitService,
                                  CompagnieService compagnieService,
                                  VolDetailService volDetailService) {
        this.produitExtraService = produitExtraService;
        this.venteProduitService = venteProduitService;
        this.compagnieService = compagnieService;
        this.volDetailService = volDetailService;
    }

    // ==================== PRODUITS ====================

    @GetMapping
    public String index(@RequestParam(value = "compagnieId", required = false) Long compagnieId, Model model) {
        if (compagnieId != null) {
            model.addAttribute("produits", produitExtraService.getByCompagnie(compagnieId));
        } else {
            model.addAttribute("produits", produitExtraService.getAll());
        }
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("selectedCompagnie", compagnieId);
        return "views/produits-extra/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("produit", new ProduitExtra());
        model.addAttribute("compagnies", compagnieService.getAll());
        return "views/produits-extra/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam("nom") String nom,
                       @RequestParam("description") String description,
                       @RequestParam("prixUnitaire") BigDecimal prixUnitaire,
                       @RequestParam("compagnieId") Long compagnieId,
                       RedirectAttributes redirectAttributes) {
        
        ProduitExtra produit = new ProduitExtra();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrixUnitaire(prixUnitaire);
        produit.setCompagnie(compagnieService.getById(compagnieId));
        
        produitExtraService.save(produit);
        redirectAttributes.addFlashAttribute("success", "Produit créé avec succès !");
        return "redirect:/produits-extra";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        ProduitExtra produit = produitExtraService.getById(id).orElse(null);
        if (produit == null) {
            return "redirect:/produits-extra";
        }
        model.addAttribute("produit", produit);
        model.addAttribute("compagnies", compagnieService.getAll());
        return "views/produits-extra/form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam("nom") String nom,
                         @RequestParam("description") String description,
                         @RequestParam("prixUnitaire") BigDecimal prixUnitaire,
                         @RequestParam("compagnieId") Long compagnieId,
                         RedirectAttributes redirectAttributes) {
        
        ProduitExtra produit = produitExtraService.getById(id).orElse(null);
        if (produit != null) {
            produit.setNom(nom);
            produit.setDescription(description);
            produit.setPrixUnitaire(prixUnitaire);
            produit.setCompagnie(compagnieService.getById(compagnieId));
            produitExtraService.save(produit);
            redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès !");
        }
        return "redirect:/produits-extra";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitExtraService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer ce produit (ventes existantes).");
        }
        return "redirect:/produits-extra";
    }

    // ==================== VENTES ====================

    @GetMapping("/ventes")
    public String ventes(@RequestParam(value = "volDetailId", required = false) Long volDetailId, Model model) {
        if (volDetailId != null) {
            model.addAttribute("ventes", venteProduitService.getByVolDetail(volDetailId));
        } else {
            model.addAttribute("ventes", venteProduitService.getAll());
        }
        model.addAttribute("volDetails", volDetailService.getAll());
        model.addAttribute("selectedVolDetail", volDetailId);
        return "views/produits-extra/ventes";
    }

    @GetMapping("/ventes/create")
    public String createVenteForm(Model model) {
        model.addAttribute("produits", produitExtraService.getAll());
        model.addAttribute("volDetails", volDetailService.getAll());
        return "views/produits-extra/vente-form";
    }

    @PostMapping("/ventes/save")
    public String saveVente(@RequestParam("produitId") Long produitId,
                            @RequestParam("volDetailId") Long volDetailId,
                            @RequestParam("quantite") Integer quantite,
                            RedirectAttributes redirectAttributes) {
        
        ProduitExtra produit = produitExtraService.getById(produitId).orElse(null);
        if (produit == null) {
            redirectAttributes.addFlashAttribute("error", "Produit introuvable.");
            return "redirect:/produits-extra/ventes";
        }
        
        VenteProduit vente = new VenteProduit();
        vente.setProduit(produit);
        vente.setVolDetail(volDetailService.getById(volDetailId));
        vente.setQuantite(quantite);
        vente.setPrixUnitaireVente(produit.getPrixUnitaire()); // Prix au moment de la vente
        vente.setDateVente(LocalDateTime.now());
        
        venteProduitService.save(vente);
        redirectAttributes.addFlashAttribute("success", "Vente enregistrée avec succès !");
        return "redirect:/produits-extra/ventes";
    }

    @GetMapping("/ventes/delete/{id}")
    public String deleteVente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        venteProduitService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Vente supprimée avec succès !");
        return "redirect:/produits-extra/ventes";
    }
}
