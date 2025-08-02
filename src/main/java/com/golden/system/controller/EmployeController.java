package com.golden.system.controller;

import com.golden.system.entity.Employe;
import com.golden.system.service.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employes")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listEmployes(Model model) {
        model.addAttribute("employes", employeService.getAllEmployes());
        return "employe/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employe", new Employe());
        model.addAttribute("roles", Employe.Role.values());
        return "employe/form";
    }

    @PostMapping
    public String createEmploye(@ModelAttribute Employe employe) {
        employeService.createEmploye(employe);
        return "redirect:/employes";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        employeService.getEmployeById(id).ifPresent(employe -> {
            model.addAttribute("employe", employe);
            model.addAttribute("roles", Employe.Role.values());
        });
        return "employe/form";
    }

    @PostMapping("/edit/{id}")
    public String updateEmploye(@PathVariable Long id, @ModelAttribute Employe employe) {
        // Only encode password if it's changed (not empty)
        if (!employe.getMotDePasse().isEmpty()) {
            employe.setMotDePasse(passwordEncoder.encode(employe.getMotDePasse()));
        } else {
            // Keep existing password
            employeService.getEmployeById(id).ifPresent(existing -> 
                employe.setMotDePasse(existing.getMotDePasse())
            );
        }
        employeService.updateEmploye(id, employe);
        return "redirect:/employes";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmploye(@PathVariable Long id) {
        employeService.deleteEmploye(id);
        return "redirect:/employes";
    }
}
