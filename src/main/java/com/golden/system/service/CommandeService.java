package com.golden.system.service;

import com.golden.system.entity.Commande;
import com.golden.system.entity.LigneDeCommande;
import com.golden.system.entity.Produit;
import com.golden.system.repository.CommandeRepository;
import com.golden.system.repository.LigneDeCommandeRepository;
import com.golden.system.repository.ProduitRepository;
import com.golden.system.dto.OrderSubmissionRequest.OrderItemRequest;
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
    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

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
    public Commande createOrder(Commande commande, List<OrderItemRequest> items) {
        // Save the order first
        Commande savedCommande = save(commande);

        // Create and save line items
        for (OrderItemRequest item : items) {
            Produit produit = produitRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // Verify stock availability
            if (produit.getStockDisponible() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + produit.getNomProduit());
            }

            // Create line item
            LigneDeCommande ligne = new LigneDeCommande();
            ligne.setCommande(savedCommande);
            ligne.setProduit(produit);
            ligne.setQuantite(item.getQuantity());
            // Note: sousTotal will be calculated automatically by @PrePersist
            ligneDeCommandeRepository.save(ligne);

            // Update stock
            produit.setStockDisponible(produit.getStockDisponible() - item.getQuantity());
            produitRepository.save(produit);
        }

        return savedCommande;
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
