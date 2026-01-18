package com.aero.ops.controller;

import com.aero.ops.model.ClasseSiege;
import com.aero.ops.service.ClasseSiegeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/classes")
public class ClasseSiegeController {

    private final ClasseSiegeService classeSiegeService;

    public ClasseSiegeController(ClasseSiegeService classeSiegeService) {
        this.classeSiegeService = classeSiegeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("classes", classeSiegeService.getAll());
        return "views/classes/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("classe", new ClasseSiege());
        return "views/classes/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute ClasseSiege classe) {
        classeSiegeService.create(classe);
        return "redirect:/classes";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        ClasseSiege classe = classeSiegeService.getById(id);
        if (classe == null) {
            return "redirect:/classes";
        }
        model.addAttribute("classe", classe);
        return "views/classes/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute ClasseSiege classe) {
        classe.setIdClasse(id);
        classeSiegeService.update(classe);
        return "redirect:/classes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        classeSiegeService.delete(id);
        return "redirect:/classes";
    }
}
