package com.golden.sytem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom du produit doit contenir entre 2 et 100 caractères")
    @Column(nullable = false)
    private String nomProduit;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Le type de produit est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeProduit type;

    @Column
    private String imageProduit;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible = 0;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix unitaire ne peut pas être négatif")
    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;

    @PrePersist
    @PreUpdate
    public void ensureStockNotNull() {
        if (stockDisponible == null) {
            stockDisponible = 0;
        }
    }

    public enum TypeProduit {
        DESSERT,
        BOISSON,
        PLAT,
        ICECREAM
    }
}