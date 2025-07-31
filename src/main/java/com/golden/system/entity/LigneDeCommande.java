package com.golden.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_de_commande")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneDeCommande {
    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantite;

    @Column(name = "sous_total")
    private BigDecimal sousTotal;

    @OneToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    @PrePersist
    @PreUpdate
    private void calculateSousTotal() {
        if (quantite != null && produit != null && produit.getPrixUnitaire() != null) {
            this.sousTotal = produit.getPrixUnitaire().multiply(BigDecimal.valueOf(quantite));
        }
    }
}
