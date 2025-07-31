package com.golden.system.controller;

import com.golden.system.entity.Produit;
import com.golden.system.entity.TypeProduit;
import com.golden.system.service.ProduitService;
import java.math.BigDecimal;
import com.golden.system.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.golden.system.service.RestaurantTableService;
import org.springframework.web.util.UriComponents;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/produits")
public class ProduitWebController {

    @Autowired
    private ProduitService produitService;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @ModelAttribute("types")
    public TypeProduit[] typeProduits() {
        return TypeProduit.values();
    }

    @ModelAttribute("typeOptions")
    public List<Map<String, String>> typeOptions() {
        List<Map<String, String>> options = new ArrayList<>();
        for (TypeProduit type : TypeProduit.values()) {
            Map<String, String> option = new HashMap<>();
            option.put("value", type.name());
            option.put("display", type.getDisplayName());
            options.add(option);
        }
        return options;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(TypeProduit.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                    return;
                }
                try {
                    setValue(TypeProduit.valueOf(text.trim()));
                } catch (IllegalArgumentException e) {
                    // Handle legacy value conversion
                    if ("PLAT".equals(text.trim())) {
                        setValue(TypeProduit.PLAT);
                    } else {
                        setValue(null);
                    }
                }
            }
        });
    }

    @GetMapping
    public String listProduits(Model model) {
        model.addAttribute("produits", produitService.findAll());
        return "produit/list";
    }

    @Autowired
    private RestaurantTableService restaurantTableService;

    @Autowired
    private CommandeService commandeService;

    @GetMapping("/gallery")
    public String showGallery(Model model) {
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("tables", restaurantTableService.findAll());

        // Get current active order
        commandeService.getCurrentActiveOrder().ifPresent(activeOrder -> {
            model.addAttribute("activeOrder", activeOrder);
            model.addAttribute("cartItems", commandeService.getLignesDeCommande(activeOrder.getId()));
            // Calculate cart total
            BigDecimal total = commandeService.getLignesDeCommande(activeOrder.getId())
                .stream()
                .<BigDecimal>map(item -> item.getProduit().getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("cartTotal", total);
        });

        return "gallery/index";
    }

    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("produit", new Produit());
        return "produit/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Produit> produit = produitService.findById(id);
        if (produit.isPresent()) {
            model.addAttribute("produit", produit.get());
            return "produit/form";
        }
        return "redirect:/produits";
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save")
    public String saveProduit(
            @Valid Produit produit,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "produit/form";
        }

        try {
            if (produit.getId() != null) {
                Optional<Produit> existingProduit = produitService.findById(produit.getId());
                if (existingProduit.isPresent() && (imageFile == null || imageFile.isEmpty())) {
                    produit.setImageProduit(existingProduit.get().getImageProduit());
                }
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                String filename = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uniqueFilename = UUID.randomUUID().toString() + "-" + filename;

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Store the correct path for static resource serving
                produit.setImageProduit("/images/products/" + uniqueFilename);
            }

            produitService.save(produit);
            redirectAttributes.addFlashAttribute("message", "Produit enregistré avec succès");
            return "redirect:/produits";

        } catch (IOException e) {
            result.rejectValue("imageProduit", "error.image", "Erreur lors du traitement de l'image");
            return "produit/form";
        } catch (Exception e) {
            result.rejectValue("global", "error.global", "Erreur lors de l'enregistrement du produit");
            return "produit/form";
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        produitService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Produit supprimé avec succès");
        return "redirect:/produits";
    }

    // For backward compatibility with POST requests
    @PostMapping("/delete/{id}")
    public String deleteProductPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return deleteProduit(id, redirectAttributes);
    }
}
