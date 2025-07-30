package com.golden.sytem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "paniers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Panier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneDeCommande> lignearticles = new ArrayList<>();

    public void ajouterProduit(Produit produit, int quantite) {
        for (LigneDeCommande ligne : lignearticles) {
            if (ligne.getProduit().getId().equals(produit.getId())) {
                ligne.setQuantite(ligne.getQuantite() + quantite);
                return;
            }
        }
        LigneDeCommande nouvelleLigne = new LigneDeCommande();
        nouvelleLigne.setProduit(produit);
        nouvelleLigne.setQuantite(quantite);
        nouvelleLigne.setPanier(this);
        lignearticles.add(nouvelleLigne);
    }

    public void supprimerProduit(Long produitId) {
        lignearticles.removeIf(ligne -> ligne.getProduit().getId().equals(produitId));
    }

    public void modifierQuantite(Long produitId, int nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            supprimerProduit(produitId);
            return;
        }
        
        for (LigneDeCommande ligne : lignearticles) {
            if (ligne.getProduit().getId().equals(produitId)) {
                ligne.setQuantite(nouvelleQuantite);
                return;
            }
        }
    }

    public BigDecimal calculerTotal() {
        return lignearticles.stream()
            .map(LigneDeCommande::getSousTotal)
            .filter(total -> total != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void viderPanier() {
        lignearticles.clear();
    }
}
