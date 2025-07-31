package com.golden.system.config;

import com.golden.system.entity.Employe;
import com.golden.system.service.EmployeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private EmployeService employeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        logger.info("Starting DataInitializer...");
        try {
            // Create default admin if no employees exist
            if (employeService.getAllEmployes().isEmpty()) {
                logger.info("No employees found, creating default admin user...");
                Employe admin = new Employe();
                admin.setNom("Admin");
                admin.setPrenom("System");
                admin.setLogin("admin");
                String encodedPassword = passwordEncoder.encode("admin123");
                admin.setMotDePasse(encodedPassword);
                admin.setRole(Employe.Role.ADMIN);
                employeService.createEmploye(admin);
                logger.info("Default admin user created successfully with login: admin");
            } else {
                logger.info("Employees already exist, skipping admin creation");
            }
        } catch (Exception e) {
            logger.error("Error creating default admin user", e);
            throw e;
        }
    }
}
