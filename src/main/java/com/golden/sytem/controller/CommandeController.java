package com.golden.sytem.controller;

import com.golden.sytem.entity.Commande;
import com.golden.sytem.entity.LigneDeCommande;
import com.golden.sytem.entity.Panier;
import com.golden.sytem.entity.Produit;
import com.golden.sytem.entity.RestaurantTable;
import com.golden.sytem.service.CommandeService;
import com.golden.sytem.service.LigneDeCommandeService;
import com.golden.sytem.service.ProduitService;
import com.golden.sytem.service.RestaurantTableService;
import com.golden.sytem.service.PanierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/commandes")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private LigneDeCommandeService ligneDeCommandeService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private PanierService panierService;

    @PostMapping("/create")
    public String createOrder(@RequestParam Long tableId,
                          @RequestParam(required = false) String notes,
                          RedirectAttributes redirectAttributes) {
        try {
            // Get the table
            RestaurantTable table = restaurantTableService.findById(tableId)
                    .orElseThrow(() -> new RuntimeException("Selected table not found"));

            // Get the current cart
            Panier panier = panierService.getPanierCourant();
            if (panier.getLignearticles().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            // Create new order
            Commande commande = new Commande();
            commande.setDateCommande(LocalDate.now());
            commande.setHeureCommande(LocalTime.now());
            commande.setStatut(Commande.StatutCommande.EN_COURS);
            commande.setTable(table);
            commande.setNotes(notes);
            
            // Save the order first to get its ID
            commande = commandeService.save(commande);

            // Add all cart items to the order
            for (LigneDeCommande cartItem : panier.getLignearticles()) {
                LigneDeCommande orderItem = new LigneDeCommande();
                orderItem.setProduit(cartItem.getProduit());
                orderItem.setQuantite(cartItem.getQuantite());
                commandeService.addLigneDeCommande(commande.getId(), orderItem);
            }

            // Clear the cart
            panier.viderPanier();
            panierService.save(panier);

            redirectAttributes.addFlashAttribute("message", "Order placed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating order: " + e.getMessage());
        }

        return "redirect:/gallery";
    }

    @PostMapping("/ligne")
    public String addLigneDeCommande(@RequestParam Long produitId,
                                   @RequestParam Integer quantite,
                                   @RequestParam Long tableId,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Get the table
            RestaurantTable table = restaurantTableService.findById(tableId)
                    .orElseThrow(() -> new RuntimeException("Selected table not found"));

            // Get or create current active order
            Commande commande = commandeService.getCurrentActiveOrder()
                    .orElseGet(() -> {
                        Commande newCommande = new Commande();
                        newCommande.setDateCommande(LocalDate.now());
                        newCommande.setHeureCommande(LocalTime.now());
                        newCommande.setStatut(Commande.StatutCommande.EN_COURS);
                        newCommande.setTable(table);
                        return commandeService.save(newCommande);
                    });

            // For existing orders, verify table consistency
            if (commande.getTable() == null) {
                commande.setTable(table);
                commande = commandeService.save(commande);
            } else if (!commande.getTable().getNumero().equals(tableId)) {
                throw new RuntimeException("Cannot change table for an existing order");
            }

            // Get the product
            Produit produit = produitService.findById(produitId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Create and save the line item
            LigneDeCommande ligneDeCommande = new LigneDeCommande();
            ligneDeCommande.setProduit(produit);
            ligneDeCommande.setQuantite(quantite);
            commandeService.addLigneDeCommande(commande.getId(), ligneDeCommande);

            redirectAttributes.addFlashAttribute("message", "Item added to order successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding item to order: " + e.getMessage());
        }

        return "redirect:/gallery";
    }
}
