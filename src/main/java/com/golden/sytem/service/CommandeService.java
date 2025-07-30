package com.golden.sytem.service;

import com.golden.sytem.entity.Commande;
import com.golden.sytem.entity.LigneDeCommande;
import com.golden.sytem.entity.Produit;
import com.golden.sytem.repository.CommandeRepository;
import com.golden.sytem.repository.LigneDeCommandeRepository;
import com.golden.sytem.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneDeCommandeRepository ligneDeCommandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Transactional(readOnly = true)
    public List<LigneDeCommande> getLignesDeCommande(Long commandeId) {
        return ligneDeCommandeRepository.findByCommandeId(commandeId);
    }

    @Transactional
    public Commande save(Commande commande) {
        try {
            System.out.println("Service - Saving commande: " + commande);
            Commande saved = commandeRepository.save(commande);
            System.out.println("Service - Saved commande successfully: " + saved);
            return saved;
        } catch (Exception e) {
            System.err.println("Service - Error saving commande: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Commande> getCurrentActiveOrder() {
        return commandeRepository.findFirstByStatutOrderByIdDesc(Commande.StatutCommande.EN_COURS);
    }

    @Transactional
    public LigneDeCommande addLigneDeCommande(Long commandeId, LigneDeCommande ligneDeCommande) {
        try {
            System.out.println("Adding ligne de commande for commande ID: " + commandeId);
            System.out.println("Ligne de commande details: " + ligneDeCommande);

            Commande commande = commandeRepository.findById(commandeId)
                    .orElseThrow(() -> new RuntimeException("Commande not found"));
            System.out.println("Found commande: " + commande);
            
            Produit produit = produitRepository.findById(ligneDeCommande.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit not found"));
            System.out.println("Found produit: " + produit);
            
            ligneDeCommande.setCommande(commande);
            ligneDeCommande.setProduit(produit);
            
            LigneDeCommande saved = ligneDeCommandeRepository.save(ligneDeCommande);
            System.out.println("Successfully saved ligne de commande: " + saved);
            return saved;
        } catch (Exception e) {
            System.err.println("Error adding ligne de commande: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Commande getCommande(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));
    }
}
