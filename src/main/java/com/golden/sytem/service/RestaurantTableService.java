package com.golden.sytem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.golden.sytem.entity.RestaurantTable;
import com.golden.sytem.repository.RestaurantTableRepository;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantTableService {
    
    @Autowired
    private RestaurantTableRepository tableRepository;
    
    public List<RestaurantTable> findAll() {
        return tableRepository.findAll();
    }
    
    public Optional<RestaurantTable> findById(Long id) {
        return tableRepository.findById(id);
    }
    
    public RestaurantTable save(RestaurantTable table) {
        return tableRepository.save(table);
    }
    
    public void deleteById(Long id) {
        tableRepository.deleteById(id);
    }
}
