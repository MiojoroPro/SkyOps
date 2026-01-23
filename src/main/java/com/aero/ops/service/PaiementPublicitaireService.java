package com.aero.ops.service;

import com.aero.ops.dto.FactureSocieteDTO;
import com.aero.ops.dto.RepartitionPaiementDTO;
import com.aero.ops.model.DiffusionPublicitaire;
import com.aero.ops.model.PaiementPublicitaire;
import com.aero.ops.model.SocieteAnnonceur;
import com.aero.ops.repository.PaiementPublicitaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaiementPublicitaireService {

    private final PaiementPublicitaireRepository paiementPublicitaireRepository;
    private final DiffusionPublicitaireService diffusionPublicitaireService;
    private final SocieteAnnonceurService societeAnnonceurService;

    public PaiementPublicitaireService(PaiementPublicitaireRepository paiementPublicitaireRepository,
                                        DiffusionPublicitaireService diffusionPublicitaireService,
                                        SocieteAnnonceurService societeAnnonceurService) {
        this.paiementPublicitaireRepository = paiementPublicitaireRepository;
        this.diffusionPublicitaireService = diffusionPublicitaireService;
        this.societeAnnonceurService = societeAnnonceurService;
    }

    public List<PaiementPublicitaire> getAll() {
        return paiementPublicitaireRepository.findAll();
    }

    public PaiementPublicitaire getById(Long id) {
        return paiementPublicitaireRepository.findById(id).orElse(null);
    }

    public List<PaiementPublicitaire> getByDiffusion(Long idDiffusion) {
        return paiementPublicitaireRepository.findByDiffusionIdDiffusion(idDiffusion);
    }

    public BigDecimal getTotalPayeByDiffusion(Long idDiffusion) {
        return paiementPublicitaireRepository.getTotalPayeByDiffusion(idDiffusion);
    }

    public PaiementPublicitaire create(PaiementPublicitaire paiement) {
        return paiementPublicitaireRepository.save(paiement);
    }

    public PaiementPublicitaire update(PaiementPublicitaire paiement) {
        return paiementPublicitaireRepository.save(paiement);
    }

    public void delete(Long id) {
        paiementPublicitaireRepository.deleteById(id);
    }
    
    /**
     * Récupère toutes les factures groupées par société
     */
    public List<FactureSocieteDTO> getFacturesParSociete() {
        List<DiffusionPublicitaire> allDiffusions = diffusionPublicitaireService.getAll();
        Map<Long, FactureSocieteDTO> facturesMap = new HashMap<>();
        
        for (DiffusionPublicitaire diff : allDiffusions) {
            if (diff.getPublicite() != null && diff.getPublicite().getSociete() != null) {
                SocieteAnnonceur societe = diff.getPublicite().getSociete();
                Long idSociete = societe.getIdSociete();
                
                if (!facturesMap.containsKey(idSociete)) {
                    facturesMap.put(idSociete, new FactureSocieteDTO(societe));
                }
                facturesMap.get(idSociete).addDiffusion(diff);
            }
        }
        
        return new ArrayList<>(facturesMap.values());
    }
    
    /**
     * Récupère la facture d'une société spécifique
     */
    public FactureSocieteDTO getFactureBySociete(Long idSociete) {
        SocieteAnnonceur societe = societeAnnonceurService.getById(idSociete);
        if (societe == null) {
            return null;
        }
        
        List<DiffusionPublicitaire> diffusions = diffusionPublicitaireService.getBySociete(idSociete);
        FactureSocieteDTO facture = new FactureSocieteDTO(societe);
        
        for (DiffusionPublicitaire diff : diffusions) {
            facture.addDiffusion(diff);
        }
        
        return facture;
    }
    
    /**
     * Simule la répartition d'un paiement au prorata sans l'enregistrer
     * @param idSociete ID de la société
     * @param montantPaiement Montant total à répartir
     * @return Liste des répartitions prévues
     */
    public List<RepartitionPaiementDTO> simulerPaiementProrata(Long idSociete, BigDecimal montantPaiement) {
        FactureSocieteDTO facture = getFactureBySociete(idSociete);
        if (facture == null || facture.getDiffusions().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<RepartitionPaiementDTO> repartitions = new ArrayList<>();
        BigDecimal resteAPayer = facture.getResteAPayer();
        
        // Si le montant du paiement dépasse le reste à payer, on le limite
        if (montantPaiement.compareTo(resteAPayer) > 0) {
            montantPaiement = resteAPayer;
        }
        
        // Calculer le pourcentage du paiement par rapport au reste à payer
        BigDecimal pourcentagePaiement = BigDecimal.ZERO;
        if (resteAPayer.compareTo(BigDecimal.ZERO) > 0) {
            pourcentagePaiement = montantPaiement.divide(resteAPayer, 6, RoundingMode.HALF_UP);
        }
        
        for (DiffusionPublicitaire diff : facture.getDiffusions()) {
            BigDecimal resteDiffusion = diff.getResteAPayer();
            
            // Si cette diffusion a encore un reste à payer
            if (resteDiffusion.compareTo(BigDecimal.ZERO) > 0) {
                // Montant réparti = reste de la diffusion * pourcentage du paiement
                BigDecimal montantReparti = resteDiffusion.multiply(pourcentagePaiement)
                        .setScale(2, RoundingMode.HALF_UP);
                
                // Poids en pourcentage de cette facture
                BigDecimal poids = resteDiffusion.multiply(BigDecimal.valueOf(100))
                        .divide(resteAPayer, 2, RoundingMode.HALF_UP);
                
                String volInfo = "-";
                if (diff.getVolDetail() != null && diff.getVolDetail().getVol() != null) {
                    volInfo = diff.getVolDetail().getVol().getNumeroVol();
                }
                
                RepartitionPaiementDTO rep = new RepartitionPaiementDTO(
                        diff.getIdDiffusion(),
                        diff.getPublicite() != null ? diff.getPublicite().getTitre() : "-",
                        volInfo,
                        diff.getMontantTotal(),
                        poids,
                        montantReparti,
                        diff.getMontantPaye()
                );
                repartitions.add(rep);
            }
        }
        
        return repartitions;
    }
    
    /**
     * Effectue un paiement au prorata sur toutes les diffusions d'une société
     * @param idSociete ID de la société
     * @param montantPaiement Montant total à répartir
     * @param reference Référence du paiement
     * @param modePaiement Mode de paiement
     * @return Liste des paiements créés
     */
    @Transactional
    public List<PaiementPublicitaire> payerProrata(Long idSociete, BigDecimal montantPaiement, 
                                                    String reference, String modePaiement) {
        List<RepartitionPaiementDTO> repartitions = simulerPaiementProrata(idSociete, montantPaiement);
        List<PaiementPublicitaire> paiements = new ArrayList<>();
        
        String refBase = reference != null && !reference.isEmpty() ? reference : "PRORATA-" + System.currentTimeMillis();
        int index = 1;
        
        for (RepartitionPaiementDTO rep : repartitions) {
            if (rep.getMontantReparti().compareTo(BigDecimal.ZERO) > 0) {
                DiffusionPublicitaire diffusion = diffusionPublicitaireService.getById(rep.getIdDiffusion());
                
                PaiementPublicitaire paiement = new PaiementPublicitaire();
                paiement.setDiffusion(diffusion);
                paiement.setMontant(rep.getMontantReparti());
                paiement.setDatePaiement(LocalDateTime.now());
                paiement.setReference(refBase + "-" + index);
                paiement.setModePaiement(modePaiement);
                
                paiements.add(paiementPublicitaireRepository.save(paiement));
                index++;
            }
        }
        
        return paiements;
    }
}
