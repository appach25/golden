package com.golden.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.golden.system.entity.RestaurantTable;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
}
