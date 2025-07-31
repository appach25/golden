package com.golden.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.golden.system.entity.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
}
