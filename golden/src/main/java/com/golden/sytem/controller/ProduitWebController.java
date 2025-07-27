package com.golden.sytem.controller;

import com.golden.sytem.entity.Produit;
import com.golden.sytem.entity.Produit.TypeProduit;
import com.golden.sytem.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/produits")
public class ProduitWebController {

    @Autowired
    private ProduitService produitService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    // Removed unused ImageService declaration

    @GetMapping("/nouveau")
    public String showProductForm(Model model) {
        model.addAttribute("produit", new Produit());
        model.addAttribute("types", TypeProduit.values());
        return "produit/form";
    }

    @PostMapping("/save")
    public String saveProduit(
            @Valid Produit produit,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("types", TypeProduit.values());
            return "produit/form";
        }

        try {
            if (!imageFile.isEmpty()) {
                String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
                // Create upload directory if it doesn't exist
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Save the file with a UUID-based name to avoid conflicts
                String uniqueFilename = java.util.UUID.randomUUID().toString() + "-" + filename;
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                produit.setImageProduit("images/products/" + uniqueFilename);
            }

            produitService.save(produit);
            redirectAttributes.addFlashAttribute("message", "Produit enregistré avec succès");
            return "redirect:/gallery";

        } catch (IOException e) {
            result.rejectValue("imageProduit", "error.image", "Erreur lors du traitement de l'image");
            model.addAttribute("types", TypeProduit.values());
            return "produit/form";
        } catch (Exception e) {
            result.rejectValue("global", "error.global", "Erreur lors de l'enregistrement du produit");
            model.addAttribute("types", TypeProduit.values());
            return "produit/form";
        }
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Produit produit = produitService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        
        model.addAttribute("produit", produit);
        model.addAttribute("types", TypeProduit.values());
        return "produit/form";
    }

    @GetMapping
    public String listProduits(Model model) {
        model.addAttribute("produits", produitService.findAll());
        return "produit/list";
    }
}
