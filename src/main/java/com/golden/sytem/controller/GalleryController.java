package com.golden.sytem.controller;

import com.golden.sytem.entity.Commande;
import com.golden.sytem.entity.LigneDeCommande;
import com.golden.sytem.entity.Panier;
import com.golden.sytem.entity.Produit;
import com.golden.sytem.entity.RestaurantTable;
import com.golden.sytem.service.CommandeService;
import com.golden.sytem.service.PanierService;
import com.golden.sytem.service.ProduitService;
import com.golden.sytem.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private PanierService panierService;

    @GetMapping
    public String showGallery(Model model) {
        List<Produit> produits = produitService.findAll();
        model.addAttribute("produits", produits);

        // Get current cart
        Panier panier = panierService.getPanierCourant();
        if (panier != null) {
            model.addAttribute("panier", panier);
            model.addAttribute("cartItems", panier.getLignearticles());
            model.addAttribute("cartTotal", panier.calculerTotal());
            
            // Get active order if exists
            Commande activeOrder = commandeService.getCurrentActiveOrder().orElse(null);
            model.addAttribute("activeOrder", activeOrder);
        } else {
            model.addAttribute("cartItems", Collections.emptyList());
            model.addAttribute("cartTotal", BigDecimal.ZERO);
            model.addAttribute("activeOrder", null);
        }

        return "gallery/index";
    }
}
