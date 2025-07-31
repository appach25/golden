package com.golden.system.repository;

import com.golden.system.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    Optional<Commande> findFirstByStatutOrderByIdDesc(Commande.StatutCommande statut);
}
