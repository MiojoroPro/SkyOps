package com.aero.ops.controller;

import com.aero.ops.model.Avion;
import com.aero.ops.repository.AvionRepository;
import com.aero.ops.repository.CompagnieRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/avions")
public class AvionController {

    private final AvionRepository avionRepository;
    private final CompagnieRepository compagnieRepository;

    public AvionController(AvionRepository avionRepository,
                           CompagnieRepository compagnieRepository) {
        this.avionRepository = avionRepository;
        this.compagnieRepository = compagnieRepository;
    }

    // LIST
    @GetMapping
    public String list(Model model) {
        model.addAttribute("avions", avionRepository.findAll());
        return "views/avions/list";
    }

    // ADD
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("avion", new Avion());
        model.addAttribute("compagnies", compagnieRepository.findAll());
        return "views/avions/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Avion avion) {
        avionRepository.save(avion);
        return "redirect:/avions";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("avion", avionRepository.findById(id).orElseThrow());
        model.addAttribute("compagnies", compagnieRepository.findAll());
        return "views/avions/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute Avion avion) {
        avion.setIdAvion(id);
        avionRepository.save(avion);
        return "redirect:/avions";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        avionRepository.deleteById(id);
        return "redirect:/avions";
    }
}
