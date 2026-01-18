package com.aero.ops.controller;

import com.aero.ops.model.CategorieAge;
import com.aero.ops.service.CategorieAgeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategorieAgeController {

    private final CategorieAgeService categorieAgeService;

    public CategorieAgeController(CategorieAgeService categorieAgeService) {
        this.categorieAgeService = categorieAgeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categorieAgeService.getAll());
        return "views/categories/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("categorie", new CategorieAge());
        return "views/categories/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute CategorieAge categorie) {
        categorieAgeService.create(categorie);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        CategorieAge categorie = categorieAgeService.getById(id);
        if (categorie == null) {
            return "redirect:/categories";
        }
        model.addAttribute("categorie", categorie);
        return "views/categories/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute CategorieAge categorie) {
        categorie.setIdCategorie(id);
        categorieAgeService.update(categorie);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categorieAgeService.delete(id);
        return "redirect:/categories";
    }
}
