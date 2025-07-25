package com.golden.sytem.controller;

import com.golden.sytem.entity.Produit;
import com.golden.sytem.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/produits")
public class ProduitController {

    private final ProduitService produitService;
    private static final String UPLOAD_DIR = "./uploads/";
    private static final Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();

    @Autowired
    public ProduitController(ProduitService produitService) {
        this.produitService = produitService;
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @GetMapping
    public String listProduits(Model model) {
        model.addAttribute("produits", produitService.getAllProduits());
        model.addAttribute("types", Produit.TypeProduit.values());
        return "produit/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Produit produit = new Produit();
        model.addAttribute("produit", produit);
        model.addAttribute("types", Produit.TypeProduit.values());
        return "produit/form";
    }

    @PostMapping("/save")
    public String saveProduit(@ModelAttribute Produit produit, 
                            @RequestParam("imageFile") MultipartFile imageFile,
                            RedirectAttributes redirectAttributes) {
        try {
            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName));
                produit.setImageProduit(fileName);
            }
            
            produitService.saveProduit(produit);
            redirectAttributes.addFlashAttribute("message", "Product saved successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload image: " + e.getMessage());
        }
        return "redirect:/produits";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        produitService.getProduitById(id).ifPresent(produit -> {
            model.addAttribute("produit", produit);
            model.addAttribute("types", Produit.TypeProduit.values());
        });
        return "produit/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        produitService.getProduitById(id).ifPresent(produit -> {
            if (produit.getImageProduit() != null) {
                try {
                    Files.deleteIfExists(uploadPath.resolve(produit.getImageProduit()));
                } catch (IOException e) {
                    // Log error but continue with deletion
                }
            }
            produitService.deleteProduit(id);
        });
        redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        return "redirect:/produits";
    }
}
