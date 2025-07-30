package com.golden.sytem.controller;

import com.golden.sytem.entity.LigneDeCommande;
import com.golden.sytem.entity.Panier;
import com.golden.sytem.entity.Produit;
import com.golden.sytem.service.PanierService;
import com.golden.sytem.service.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/panier")
public class PanierController {

    @Autowired
    private PanierService panierService;

    @Autowired
    private ProduitService produitService;

    @PostMapping("/ajouter")
    @ResponseBody
    public ResponseEntity<?> ajouterAuPanier(@RequestParam Long produitId, @RequestParam int quantite) {
        Produit produit = produitService.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        
        Panier panier = panierService.getPanierCourant();
        panier.ajouterProduit(produit, quantite);
        panier = panierService.save(panier);

        return ResponseEntity.ok(panier);
    }

    @PostMapping("/modifier")
    @ResponseBody
    public ResponseEntity<?> modifierQuantite(@RequestParam Long produitId, @RequestParam int quantite) {
        Panier panier = panierService.getPanierCourant();
        panier.modifierQuantite(produitId, quantite);
        panier = panierService.save(panier);

        return ResponseEntity.ok(panier);
    }

    @PostMapping("/supprimer")
    @ResponseBody
    public ResponseEntity<?> supprimerDuPanier(@RequestParam Long produitId) {
        Panier panier = panierService.getPanierCourant();
        panier.supprimerProduit(produitId);
        panier = panierService.save(panier);

        return ResponseEntity.ok(panier);
    }

    @PostMapping("/vider")
    @ResponseBody
    public ResponseEntity<?> viderPanier() {
        Panier panier = panierService.getPanierCourant();
        panier.viderPanier();
        panier = panierService.save(panier);

        return ResponseEntity.ok(panier);
    }
}
