package com.golden.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.golden.system.entity.RestaurantTable;
import com.golden.system.repository.TableRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public Optional<RestaurantTable> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    public RestaurantTable createTable(RestaurantTable table) {
        return tableRepository.save(table);
    }

    public RestaurantTable updateTable(Long id, RestaurantTable tableDetails) {
        Optional<RestaurantTable> table = tableRepository.findById(id);
        if (table.isPresent()) {
            RestaurantTable existingTable = table.get();
            existingTable.setName(tableDetails.getName());
            existingTable.setNbPlaces(tableDetails.getNbPlaces());
            return tableRepository.save(existingTable);
        }
        return null;
    }

    public boolean deleteTable(Long id) {
        Optional<RestaurantTable> table = tableRepository.findById(id);
        if (table.isPresent()) {
            tableRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
