package com.golden.sytem.repository;

import com.golden.sytem.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {
    boolean existsByLogin(String login);
}
