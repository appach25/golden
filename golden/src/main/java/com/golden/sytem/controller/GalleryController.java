package com.golden.sytem.controller;

import com.golden.sytem.entity.Produit;
import com.golden.sytem.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    private ProduitService produitService;

    @GetMapping
    public String showGallery(Model model) {
        List<Produit> produits = produitService.findAll();
        model.addAttribute("produits", produits);
        return "gallery/index";
    }
}
