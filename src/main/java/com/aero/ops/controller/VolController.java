package com.aero.ops.controller;

import com.aero.ops.model.Vol;
import com.aero.ops.model.VolDetail;
import com.aero.ops.service.AvionService;
import com.aero.ops.service.AeroportService;
import com.aero.ops.service.CompagnieService;
import com.aero.ops.service.VolDetailService;
import com.aero.ops.service.VolService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/vol")
public class VolController {

    private final VolService volService;
    private final VolDetailService volDetailService;
    private final CompagnieService compagnieService;
    private final AeroportService aeroportService;
    private final AvionService avionService;

    public VolController(VolService volService,
                         VolDetailService volDetailService,
                         CompagnieService compagnieService,
                         AeroportService aeroportService,
                         AvionService avionService) {
        this.volService = volService;
        this.volDetailService = volDetailService;
        this.compagnieService = compagnieService;
        this.aeroportService = aeroportService;
        this.avionService = avionService;
    }

    // Liste des vols
    @GetMapping
    public String listVols(Model model) {
        model.addAttribute("vols", volService.getAll());
        return "views/vol/list";
    }

    // Ajouter un vol
    @GetMapping("/add")
    public String addVolForm(Model model) {
        model.addAttribute("vol", new Vol());
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("aeroports", aeroportService.getAll());
        return "views/vol/add";
    }

    @PostMapping("/add")
    public String saveVol(@ModelAttribute Vol vol) {
        if (vol.getCompagnie() != null && vol.getCompagnie().getIdCompagnie() != null) {
            vol.setCompagnie(compagnieService.getById(vol.getCompagnie().getIdCompagnie()));
        }
        if (vol.getAeroportDepart() != null && vol.getAeroportDepart().getIdAeroport() != null) {
            vol.setAeroportDepart(aeroportService.getById(vol.getAeroportDepart().getIdAeroport()));
        }
        if (vol.getAeroportArrivee() != null && vol.getAeroportArrivee().getIdAeroport() != null) {
            vol.setAeroportArrivee(aeroportService.getById(vol.getAeroportArrivee().getIdAeroport()));
        }
        volService.create(vol);
        return "redirect:/vol";
    }

    // Modifier un vol
    @GetMapping("/edit/{id}")
    public String editVolForm(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        model.addAttribute("vol", vol);
        model.addAttribute("compagnies", compagnieService.getAll());
        model.addAttribute("aeroports", aeroportService.getAll());
        return "views/vol/edit";
    }

    @PostMapping("/edit")
    public String updateVol(@ModelAttribute Vol vol) {
        if (vol.getCompagnie() != null && vol.getCompagnie().getIdCompagnie() != null) {
            vol.setCompagnie(compagnieService.getById(vol.getCompagnie().getIdCompagnie()));
        }
        if (vol.getAeroportDepart() != null && vol.getAeroportDepart().getIdAeroport() != null) {
            vol.setAeroportDepart(aeroportService.getById(vol.getAeroportDepart().getIdAeroport()));
        }
        if (vol.getAeroportArrivee() != null && vol.getAeroportArrivee().getIdAeroport() != null) {
            vol.setAeroportArrivee(aeroportService.getById(vol.getAeroportArrivee().getIdAeroport()));
        }
        volService.update(vol);
        return "redirect:/vol";
    }

    // Supprimer un vol
    @GetMapping("/delete/{id}")
    public String deleteVol(@PathVariable Long id) {
        volService.delete(id);
        return "redirect:/vol";
    }

    // Détails et exécutions d’un vol
    @GetMapping("/{id}/details")
    public String volDetails(@PathVariable Long id, Model model) {
        Vol vol = volService.getById(id);
        model.addAttribute("vol", vol);
        model.addAttribute("details", volDetailService.getByVol(vol));
        model.addAttribute("newDetail", new VolDetail());
        model.addAttribute("avions", avionService.getAll());
        return "views/vol/details";
    }

    // Ajouter un VolDetail
    @PostMapping("/{id}/details/add")
    public String addVolDetail(@PathVariable Long id, @ModelAttribute VolDetail volDetail) {
        Vol vol = volService.getById(id);
        volDetail.setVol(vol);
        if (volDetail.getAvion() != null && volDetail.getAvion().getIdAvion() != null) {
            volDetail.setAvion(avionService.getById(volDetail.getAvion().getIdAvion()));
        }
        volDetailService.create(volDetail);
        return "redirect:/vol/" + id + "/details";
    }

    // Supprimer un VolDetail
    @GetMapping("/details/delete/{id}")
    public String deleteVolDetail(@PathVariable Long id) {
        VolDetail detail = volDetailService.getById(id);
        Long volId = detail.getVol().getIdVol();
        volDetailService.delete(id);
        return "redirect:/vol/" + volId + "/details";
    }
}
