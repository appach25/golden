package com.golden.sytem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.golden.sytem.entity.RestaurantTable;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
}
