package com.golden.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande", nullable = false)
    private LocalDate dateCommande;

    @Column(name = "heure_commande", nullable = false)
    private LocalTime heureCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut;

    @OneToOne
    @JoinColumn(name = "table_id", nullable = true)
    private RestaurantTable table;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = true)
    private Employe employe;

    @Column(name = "grand_total", nullable = false)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(name = "notes", length = 500)
    private String notes;

    public enum StatutCommande {
        EN_COURS,
        SERVIE,
        ANNULEE,
        TERMINEE,
        NON_PAYER,
        PAYER
    }

    @PrePersist
    public void prePersist() {
        if (dateCommande == null) {
            dateCommande = LocalDate.now();
        }
        if (heureCommande == null) {
            heureCommande = LocalTime.now();
        }
        if (statut == null) {
            statut = StatutCommande.NON_PAYER;
        }
    }


}
