package com.golden.sytem.service;

import com.golden.sytem.entity.Employe;
import com.golden.sytem.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;

    public List<Employe> getAllEmployes() {
        return employeRepository.findAll();
    }

    public Optional<Employe> getEmployeById(Long id) {
        return employeRepository.findById(id);
    }

    public Employe createEmploye(Employe employe) {
        // Check if login already exists
        if (employeRepository.existsByLogin(employe.getLogin())) {
            throw new RuntimeException("Login déjà utilisé");
        }
        return employeRepository.save(employe);
    }

    public Employe updateEmploye(Long id, Employe employeDetails) {
        Optional<Employe> employe = employeRepository.findById(id);
        if (employe.isPresent()) {
            Employe existingEmploye = employe.get();
            
            // Check if new login is different and already exists
            if (!existingEmploye.getLogin().equals(employeDetails.getLogin()) && 
                employeRepository.existsByLogin(employeDetails.getLogin())) {
                throw new RuntimeException("Login déjà utilisé");
            }

            existingEmploye.setNom(employeDetails.getNom());
            existingEmploye.setPrenom(employeDetails.getPrenom());
            existingEmploye.setRole(employeDetails.getRole());
            existingEmploye.setLogin(employeDetails.getLogin());
            existingEmploye.setMotDePasse(employeDetails.getMotDePasse());
            
            return employeRepository.save(existingEmploye);
        }
        return null;
    }

    public void deleteEmploye(Long id) {
        employeRepository.deleteById(id);
    }
}
