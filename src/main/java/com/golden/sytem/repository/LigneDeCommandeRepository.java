package com.golden.sytem.repository;

import com.golden.sytem.entity.LigneDeCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneDeCommandeRepository extends JpaRepository<LigneDeCommande, Long> {
    List<LigneDeCommande> findByCommandeId(Long commandeId);
}
