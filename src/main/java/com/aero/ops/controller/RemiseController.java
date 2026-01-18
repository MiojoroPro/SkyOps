package com.aero.ops.controller;

import com.aero.ops.model.CategorieAge;
import com.aero.ops.model.ClasseSiege;
import com.aero.ops.model.RemiseClasseCategorie;
import com.aero.ops.service.CategorieAgeService;
import com.aero.ops.service.ClasseSiegeService;
import com.aero.ops.service.RemiseClasseCategorieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/remises")
public class RemiseController {

    private final RemiseClasseCategorieService remiseService;
    private final ClasseSiegeService classeService;
    private final CategorieAgeService categorieService;

    public RemiseController(RemiseClasseCategorieService remiseService,
                           ClasseSiegeService classeService,
                           CategorieAgeService categorieService) {
        this.remiseService = remiseService;
        this.classeService = classeService;
        this.categorieService = categorieService;
    }

    @GetMapping
    public String listRemises(Model model) {
        List<ClasseSiege> classes = classeService.getAll();
        List<CategorieAge> categories = categorieService.getAll();
        
        // Créer une matrice de remises
        Map<Long, Map<Long, BigDecimal>> matrice = new LinkedHashMap<>();
        for (ClasseSiege classe : classes) {
            Map<Long, BigDecimal> remisesCategorie = new LinkedHashMap<>();
            for (CategorieAge categorie : categories) {
                BigDecimal pourcentage = remiseService.getPourcentage(classe.getIdClasse(), categorie.getIdCategorie());
                remisesCategorie.put(categorie.getIdCategorie(), pourcentage);
            }
            matrice.put(classe.getIdClasse(), remisesCategorie);
        }
        
        model.addAttribute("classes", classes);
        model.addAttribute("categories", categories);
        model.addAttribute("matrice", matrice);
        return "views/remises/list";
    }

    @GetMapping("/edit")
    public String editRemisesForm(Model model) {
        List<ClasseSiege> classes = classeService.getAll();
        List<CategorieAge> categories = categorieService.getAll();
        
        // Créer une matrice de remises
        Map<Long, Map<Long, BigDecimal>> matrice = new LinkedHashMap<>();
        for (ClasseSiege classe : classes) {
            Map<Long, BigDecimal> remisesCategorie = new LinkedHashMap<>();
            for (CategorieAge categorie : categories) {
                BigDecimal pourcentage = remiseService.getPourcentage(classe.getIdClasse(), categorie.getIdCategorie());
                remisesCategorie.put(categorie.getIdCategorie(), pourcentage);
            }
            matrice.put(classe.getIdClasse(), remisesCategorie);
        }
        
        model.addAttribute("classes", classes);
        model.addAttribute("categories", categories);
        model.addAttribute("matrice", matrice);
        return "views/remises/edit";
    }

    @PostMapping("/edit")
    public String saveRemises(@RequestParam Map<String, String> allParams) {
        List<ClasseSiege> classes = classeService.getAll();
        List<CategorieAge> categories = categorieService.getAll();
        
        for (ClasseSiege classe : classes) {
            for (CategorieAge categorie : categories) {
                String key = "remise_" + classe.getIdClasse() + "_" + categorie.getIdCategorie();
                String value = allParams.get(key);
                if (value != null && !value.isBlank()) {
                    try {
                        BigDecimal pourcentage = new BigDecimal(value);
                        remiseService.save(classe.getIdClasse(), categorie.getIdCategorie(), pourcentage);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        
        return "redirect:/remises";
    }
}
