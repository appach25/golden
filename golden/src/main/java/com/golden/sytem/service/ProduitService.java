package com.golden.sytem.service;

import com.golden.sytem.entity.Produit;
import com.golden.sytem.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final ImageService imageService;

    @Autowired
    public ProduitService(ProduitRepository produitRepository, ImageService imageService) {
        this.produitRepository = produitRepository;
        this.imageService = imageService;
    }

    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public Optional<Produit> findById(Long id) {
        return produitRepository.findById(id);
    }

    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    public void delete(Long id) {
        Optional<Produit> produit = produitRepository.findById(id);
        produit.ifPresent(p -> {
            if (p.getImageProduit() != null) {
                try {
                    imageService.deleteImage(p.getImageProduit());
                } catch (java.io.IOException e) {
                    // Handle the exception, e.g., log it
                    e.printStackTrace();
                }
            }
            produitRepository.deleteById(id);
        });
    
    }  

}