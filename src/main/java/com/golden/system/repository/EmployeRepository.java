package com.golden.system.repository;

import com.golden.system.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    boolean existsByLogin(String login);
    Optional<Employe> findByLogin(String login);
}
