package com.golden.sytem.controller;

import com.golden.sytem.entity.Produit;
import com.golden.sytem.entity.RestaurantTable;
import com.golden.sytem.service.ProduitService;
import com.golden.sytem.service.RestaurantTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/gallery-interactive")
public class GalleryInteractiveController {

    @Autowired
    private ProduitService produitService;

    @Autowired
    private RestaurantTableService restaurantTableService;

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
}
