package com.golden.system.controller;

import com.golden.system.entity.Commande;
import com.golden.system.entity.Commande.StatutCommande;
import com.golden.system.entity.LigneDeCommande;
import com.golden.system.entity.Produit;
import com.golden.system.entity.RestaurantTable;
import com.golden.system.service.ProduitService;
import com.golden.system.service.RestaurantTableService;
import com.golden.system.service.CommandeService;
import com.golden.system.dto.OrderSubmissionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;

@Controller
@RequestMapping("/gallery-interactive")
public class GalleryInteractiveController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private CommandeService commandeService;

    @GetMapping
    public String showInteractiveGallery(Model model) {
        List<Produit> produits = produitService.findAll();
        List<RestaurantTable> tables = restaurantTableService.findAll();
        model.addAttribute("produits", produits);
        model.addAttribute("tables", tables);
        return "gallery/interactive";
    }

    @GetMapping("/produits/{id}")
    @ResponseBody
    public Produit getProduit(@PathVariable Long id) {
        return produitService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @PostMapping("/submit-order")
    @ResponseBody
    public Commande submitOrder(@RequestBody OrderSubmissionRequest request) {
        // Get the selected table
        RestaurantTable table = restaurantTableService.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // Create and populate the order
        Commande commande = new Commande();
        commande.setTable(table);
        commande.setDateCommande(LocalDate.now());
        commande.setHeureCommande(LocalTime.now());
        commande.setStatut(StatutCommande.NON_PAYER);
        commande.setNotes(request.getNotes());

        // Create the order and its items
        return commandeService.createOrder(commande, request.getItems());
    }
}
