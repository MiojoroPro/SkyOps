package com.aero.ops.controller;

import com.aero.ops.model.Avion;
import com.aero.ops.model.AvionClasse;
import com.aero.ops.model.ClasseSiege;
import com.aero.ops.service.AvionService;
import com.aero.ops.service.AvionClasseService;
import com.aero.ops.service.ClasseSiegeService;
import com.aero.ops.service.CompagnieService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/avions")
public class AvionController {

    private final AvionService avionService;
    private final AvionClasseService avionClasseService;
    private final ClasseSiegeService classeSiegeService;
    private final CompagnieService compagnieService;

    public AvionController(AvionService avionService,
                           AvionClasseService avionClasseService,
                           ClasseSiegeService classeSiegeService,
                           CompagnieService compagnieService) {
        this.avionService = avionService;
        this.avionClasseService = avionClasseService;
        this.classeSiegeService = classeSiegeService;
        this.compagnieService = compagnieService;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("avions", avionService.getAll());
        return "views/avions/list";
    }

    // ADD
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("avion", new Avion());
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("classes", classeSiegeService.getAll());
        return "views/avions/add";
    }

    @PostMapping("/add")
    @Transactional
    public String add(@ModelAttribute Avion avion,
                      @RequestParam Map<String, String> allParams) {
        // Résoudre la compagnie
        if (avion.getCompagnie() != null && avion.getCompagnie().getIdCompagnie() != null) {
            avion.setCompagnie(compagnieService.getById(avion.getCompagnie().getIdCompagnie()));
        }
        
        Avion saved = avionService.create(avion);
        
        // Sauvegarder les capacités par classe
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            String capaciteKey = "capacite_" + classe.getIdClasse();
            String capaciteStr = allParams.get(capaciteKey);
            if (capaciteStr != null && !capaciteStr.isBlank()) {
                try {
                    int capacite = Integer.parseInt(capaciteStr);
                    if (capacite > 0) {
                        AvionClasse ac = new AvionClasse();
                        ac.setAvion(saved);
                        ac.setClasseSiege(classe);
                        ac.setCapacite(capacite);
                        avionClasseService.save(ac);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        return "redirect:/avions";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Avion avion = avionService.getById(id);
        model.addAttribute("avion", avion);
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("classes", classeSiegeService.getAll());
        
        // Créer un Map des capacités actuelles par classe
        List<AvionClasse> avionClasses = avionClasseService.getByAvion(id);
        Map<Long, Integer> avionCapacites = new HashMap<>();
        for (AvionClasse ac : avionClasses) {
            avionCapacites.put(ac.getClasseSiege().getIdClasse(), ac.getCapacite());
        }
        model.addAttribute("avionCapacites", avionCapacites);
        
        return "views/avions/edit";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String edit(@PathVariable Long id,
                       @ModelAttribute Avion avion,
                       @RequestParam Map<String, String> allParams) {
        avion.setIdAvion(id);
        
        // Résoudre la compagnie
        if (avion.getCompagnie() != null && avion.getCompagnie().getIdCompagnie() != null) {
            avion.setCompagnie(compagnieService.getById(avion.getCompagnie().getIdCompagnie()));
        }
        
        avionService.update(avion);
        
        // Supprimer les anciennes capacités et recréer
        avionClasseService.deleteByAvion(id);
        
        for (ClasseSiege classe : classeSiegeService.getAll()) {
            String capaciteKey = "capacite_" + classe.getIdClasse();
            String capaciteStr = allParams.get(capaciteKey);
            if (capaciteStr != null && !capaciteStr.isBlank()) {
                try {
                    int capacite = Integer.parseInt(capaciteStr);
                    if (capacite > 0) {
                        AvionClasse ac = new AvionClasse();
                        ac.setAvion(avion);
                        ac.setClasseSiege(classe);
                        ac.setCapacite(capacite);
                        avionClasseService.save(ac);
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        return "redirect:/avions";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id) {
        avionClasseService.deleteByAvion(id);
        avionService.delete(id);
        return "redirect:/avions";
    }
}
