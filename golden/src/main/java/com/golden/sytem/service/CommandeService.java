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

    @Transactional
    public Commande saveCommande(Commande commande) {
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

    @Transactional
    public LigneDeCommande addLigneDeCommande(Long commandeId, LigneDeCommande ligneDeCommande) {
        Commande commande = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new RuntimeException("Commande not found"));
        
        Produit produit = produitRepository.findById(ligneDeCommande.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit not found"));
        
        ligneDeCommande.setCommande(commande);
        ligneDeCommande.setProduit(produit);
        
        return ligneDeCommandeRepository.save(ligneDeCommande);
    }

    public List<LigneDeCommande> getLignesDeCommande(Long commandeId) {
        return ligneDeCommandeRepository.findByCommandeId(commandeId);
    }

    public Commande getCommande(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));
    }
}
