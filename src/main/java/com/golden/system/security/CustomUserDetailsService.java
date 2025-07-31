package com.golden.system.security;

import com.golden.system.entity.Employe;
import com.golden.system.repository.EmployeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private EmployeRepository employeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by username: {}", username);

        try {
            Employe employe = employeRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

            logger.debug("Found user: {} with role: {}", employe.getLogin(), employe.getRole());

            return User.builder()
                .username(employe.getLogin())
                .password(employe.getMotDePasse())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + employe.getRole().name())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
        } catch (Exception e) {
            logger.error("Error loading user by username: {}", username, e);
            throw e;
        }
    }
}
