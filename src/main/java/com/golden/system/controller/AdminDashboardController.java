package com.golden.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.golden.system.service.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired private EmployeService employeService;
    @Autowired private ProduitService produitService;
    @Autowired private CommandeService commandeService;
    @Autowired private RestaurantTableService restaurantTableService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("employeCount", employeService.getAllEmployes().size());
        model.addAttribute("produitCount", produitService.findAll().size());
        model.addAttribute("commandeCount", commandeService.getAllCommandes().size());
        model.addAttribute("tableCount", restaurantTableService.findAll().size());
        return "admin/dashboard";
    }
}
