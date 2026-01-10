package com.aero.ops.controller;

import com.aero.ops.model.Compagnie;
import com.aero.ops.repository.CompagnieRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/compagnies")
public class CompagnieController {

    private final CompagnieRepository compagnieRepository;

    public CompagnieController(CompagnieRepository compagnieRepository) {
        this.compagnieRepository = compagnieRepository;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("compagnies", compagnieRepository.findAll());
        return "views/compagnies/list";
    }

    // ADD
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("compagnie", new Compagnie());
        return "views/compagnies/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Compagnie compagnie) {
        compagnieRepository.save(compagnie);
        return "redirect:/compagnies";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("compagnie", compagnieRepository.findById(id).orElseThrow());
        return "views/compagnies/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Compagnie compagnie) {
        compagnie.setIdCompagnie(id);
        compagnieRepository.save(compagnie);
        return "redirect:/compagnies";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        compagnieRepository.deleteById(id);
        return "redirect:/compagnies";
    }
}
