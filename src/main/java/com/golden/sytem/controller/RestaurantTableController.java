package com.golden.sytem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.golden.sytem.entity.RestaurantTable;
import com.golden.sytem.service.RestaurantTableService;

@Controller
@RequestMapping("/tables")
public class RestaurantTableController {
    
    @Autowired
    private RestaurantTableService tableService;
    
    @GetMapping
    public String listTables(Model model) {
        model.addAttribute("tables", tableService.findAll());
        return "table/list";
    }
    
    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("table", new RestaurantTable());
        return "table/form";
    }
    
    @PostMapping("/save")
    public String saveTable(@ModelAttribute RestaurantTable table, RedirectAttributes redirectAttributes) {
        tableService.save(table);
        redirectAttributes.addFlashAttribute("message", "Table enregistrée avec succès");
        return "redirect:/tables";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        tableService.findById(id).ifPresent(table -> model.addAttribute("table", table));
        return "table/form";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tableService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Table supprimée avec succès");
        return "redirect:/tables";
    }
}
