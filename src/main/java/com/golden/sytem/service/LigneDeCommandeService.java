package com.golden.sytem.service;

import com.golden.sytem.entity.LigneDeCommande;
import com.golden.sytem.repository.LigneDeCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LigneDeCommandeService {

    @Autowired
    private LigneDeCommandeRepository ligneDeCommandeRepository;

    @Transactional
    public LigneDeCommande save(LigneDeCommande ligneDeCommande) {
        try {
            System.out.println("Saving LigneDeCommande: " + ligneDeCommande);
            LigneDeCommande saved = ligneDeCommandeRepository.save(ligneDeCommande);
            System.out.println("Successfully saved LigneDeCommande: " + saved);
            return saved;
        } catch (Exception e) {
            System.err.println("Error saving LigneDeCommande: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
