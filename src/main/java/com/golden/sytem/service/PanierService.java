package com.golden.sytem.service;

import com.golden.sytem.entity.Panier;
import com.golden.sytem.repository.PanierRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PanierService {

    @Autowired
    private PanierRepository panierRepository;

    @Autowired
    private HttpSession httpSession;

    private static final String PANIER_SESSION_KEY = "panier_courant_id";

    @Transactional
    public Panier getPanierCourant() {
        Long panierId = (Long) httpSession.getAttribute(PANIER_SESSION_KEY);
        
        if (panierId != null) {
            return panierRepository.findById(panierId)
                    .orElseGet(this::createNewPanier);
        }
        
        return createNewPanier();
    }

    @Transactional
    public Panier save(Panier panier) {
        panier = panierRepository.save(panier);
        httpSession.setAttribute(PANIER_SESSION_KEY, panier.getId());
        return panier;
    }

    private Panier createNewPanier() {
        Panier panier = new Panier();
        return save(panier);
    }
}
