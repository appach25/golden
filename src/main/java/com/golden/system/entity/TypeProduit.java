package com.golden.system.entity;

public enum TypeProduit {
    ENTREE("Entrée"),
    PLAT("Plat Principal"),
    DESSERT("Dessert"),
    BOISSON("Boisson"),
    AUTRE("Autre");

    private final String displayName;

    TypeProduit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
