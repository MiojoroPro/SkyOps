package com.aero.ops.service;

import com.aero.ops.model.Vol;
import com.aero.ops.model.VolDetail;
import com.aero.ops.repository.VolDetailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VolDetailService {

    private final VolDetailRepository volDetailRepository;

    public VolDetailService(VolDetailRepository volDetailRepository) {
        this.volDetailRepository = volDetailRepository;
    }

    public List<VolDetail> getAll() {
        return volDetailRepository.findAll();
    }

    public VolDetail getById(Long id) {
        return volDetailRepository.findById(id).orElse(null);
    }

    public List<VolDetail> getByVol(Vol vol) {
        return volDetailRepository.findByVol(vol);
    }

    public List<VolDetail> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return volDetailRepository.findByDateHeureDepartBetween(start, end);
    }

    public VolDetail create(VolDetail volDetail) {
        // Initialize remaining places from avion if not set
        if (volDetail.getAvion() != null) {
            if (volDetail.getPlacesEcoRestantes() == null) {
                Integer capEco = volDetail.getAvion().getCapaciteEconomique();
                volDetail.setPlacesEcoRestantes(capEco != null ? capEco : 0);
            }
            if (volDetail.getPlacesPremiereRestantes() == null) {
                Integer capPrem = volDetail.getAvion().getCapacitePremiere();
                volDetail.setPlacesPremiereRestantes(capPrem != null ? capPrem : 0);
            }
            if (volDetail.getPlacesPremiumRestantes() == null) {
                Integer capPremium = volDetail.getAvion().getCapacitePremium();
                volDetail.setPlacesPremiumRestantes(capPremium != null ? capPremium : 0);
            }
        }
        // Initialize prices from vol's prices if not set
        if (volDetail.getVol() != null) {
            if (volDetail.getPrixEconomique() == null) {
                Double vEco = volDetail.getVol().getPrixEconomique() != null ? volDetail.getVol().getPrixEconomique() : volDetail.getVol().getPrixBase();
                volDetail.setPrixEconomique(vEco);
            }
            if (volDetail.getPrixPremiere() == null) {
                Double vPrem = volDetail.getVol().getPrixPremiere() != null ? volDetail.getVol().getPrixPremiere() : volDetail.getVol().getPrixBase() * 1.5;
                volDetail.setPrixPremiere(vPrem);
            }
            if (volDetail.getPrixPremium() == null) {
                Double vPremium = volDetail.getVol().getPrixPremium() != null ? volDetail.getVol().getPrixPremium() : volDetail.getVol().getPrixBase() * 1.25;
                volDetail.setPrixPremium(vPremium);
            }
        }
        return volDetailRepository.save(volDetail);
    }

    public VolDetail update(VolDetail volDetail) {
        // Ensure defaults stay set on update
        if (volDetail.getAvion() != null) {
            if (volDetail.getPlacesEcoRestantes() == null) {
                Integer capEco = volDetail.getAvion().getCapaciteEconomique();
                volDetail.setPlacesEcoRestantes(capEco != null ? capEco : 0);
            }
            if (volDetail.getPlacesPremiereRestantes() == null) {
                Integer capPrem = volDetail.getAvion().getCapacitePremiere();
                volDetail.setPlacesPremiereRestantes(capPrem != null ? capPrem : 0);
            }
            if (volDetail.getPlacesPremiumRestantes() == null) {
                Integer capPremium = volDetail.getAvion().getCapacitePremium();
                volDetail.setPlacesPremiumRestantes(capPremium != null ? capPremium : 0);
            }
        }
        if (volDetail.getVol() != null) {
            if (volDetail.getPrixEconomique() == null) {
                Double vEco = volDetail.getVol().getPrixEconomique() != null ? volDetail.getVol().getPrixEconomique() : volDetail.getVol().getPrixBase();
                volDetail.setPrixEconomique(vEco);
            }
            if (volDetail.getPrixPremiere() == null) {
                Double vPrem = volDetail.getVol().getPrixPremiere() != null ? volDetail.getVol().getPrixPremiere() : volDetail.getVol().getPrixBase() * 1.5;
                volDetail.setPrixPremiere(vPrem);
            }
            if (volDetail.getPrixPremium() == null) {
                Double vPremium = volDetail.getVol().getPrixPremium() != null ? volDetail.getVol().getPrixPremium() : volDetail.getVol().getPrixBase() * 1.25;
                volDetail.setPrixPremium(vPremium);
            }
        }
        return volDetailRepository.save(volDetail);
    }

    public void delete(Long id) {
        volDetailRepository.deleteById(id);
    }
}
