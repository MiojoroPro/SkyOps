package com.aero.ops.controller;

import com.aero.ops.model.Aeroport;
import com.aero.ops.repository.AeroportRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aeroports")
public class AeroportController {

    private final AeroportRepository aeroportRepository;

    public AeroportController(AeroportRepository aeroportRepository) {
        this.aeroportRepository = aeroportRepository;
    }

    // LIST
    @GetMapping
    public String listAeroports(Model model) {
        model.addAttribute("aeroports", aeroportRepository.findAll());
        return "views/aeroport/list";
    }

    // ADD
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("aeroport", new Aeroport());
        return "views/aeroport/add";
    }

    @PostMapping("/add")
    public String addAeroport(@ModelAttribute Aeroport aeroport) {
        aeroportRepository.save(aeroport);
        return "redirect:/aeroports";
    }

    // EDIT
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Aeroport aeroport = aeroportRepository.findById(id).orElseThrow();
        model.addAttribute("aeroport", aeroport);
        return "views/aeroport/edit";
    }

    @PostMapping("/edit/{id}")
    public String editAeroport(@PathVariable Long id, @ModelAttribute Aeroport aeroport) {
        aeroport.setIdAeroport(id);
        aeroportRepository.save(aeroport);
        return "redirect:/aeroports";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteAeroport(@PathVariable Long id) {
        aeroportRepository.deleteById(id);
        return "redirect:/aeroports";
    }
}
