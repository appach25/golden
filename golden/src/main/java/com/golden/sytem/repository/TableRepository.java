package com.golden.sytem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.golden.sytem.entity.RestaurantTable;

@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {
}
