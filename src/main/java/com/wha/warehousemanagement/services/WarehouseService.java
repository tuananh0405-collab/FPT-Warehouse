package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public ResponseObject getAllWarehouses() {
        try {
            return new ResponseObject("200", "All warehouses fetched successfully", warehouseRepository.findAll());
        } catch (Exception e) {
            return new ResponseObject("500", "Failed to fetch all warehouses", null);
        }
    }

    public ResponseObject getWarehouseById(int id) {
        try {
            return new ResponseObject("200", "Warehouse fetched successfully", warehouseRepository.findById(id));
        } catch (Exception e) {
            return new ResponseObject("500", "Failed to fetch warehouse", null);
        }
    }

    public ResponseObject addWarehouse(WarehouseDTO warehouseDTO) {
        try {
            if(warehouseRepository.existsByName(warehouseDTO.getName())){
                return new ResponseObject("400", "Warehouse with name " + warehouseDTO.getName() + " already exists", null);
            } else if (warehouseRepository.existsByAddress(warehouseDTO.getAddress())) {
                return new ResponseObject("400", "Warehouse with address " + warehouseDTO.getAddress() + " already exists", null);
            }
            Warehouse warehouse = new Warehouse();
            warehouse.setName(warehouseDTO.getName());
            warehouse.setDescription(warehouseDTO.getDescription());
            warehouse.setCreatedAt(new Date());
            warehouse.setAddress(warehouseDTO.getAddress());
            System.out.println(warehouse);
            warehouseRepository.save(warehouse);
            return new ResponseObject("200", "Warehouse added successfully", warehouse);
        } catch (Exception e) {
            return new ResponseObject("500", "Failed to add warehouse", null);
        }
    }

    public ResponseObject updateWarehouseById(int id, WarehouseDTO warehouseDTO) {
        try {
            Warehouse warehouse = warehouseRepository.findById(id).orElseThrow();
            boolean isUpdated = false;
            if (warehouseDTO.getName() != null && !warehouseDTO.getName().equals(warehouse.getName())) {
                warehouse.setName(warehouseDTO.getName());
                isUpdated = true;
            }
            if (warehouseDTO.getDescription() != null && !warehouseDTO.getDescription().equals(warehouse.getDescription())) {
                warehouse.setDescription(warehouseDTO.getDescription());
                isUpdated = true;
            }
            if (warehouseDTO.getAddress() != null && !warehouseDTO.getAddress().equals(warehouse.getAddress())) {
                warehouse.setAddress(warehouseDTO.getAddress());
                isUpdated = true;
            }
            if (isUpdated) {
                warehouse.setUpdatedAt(new Date());
                warehouseRepository.save(warehouse);
                return new ResponseObject("200", "Warehouse updated successfully", warehouse);
            } else {
                return new ResponseObject("200", "Warehouse is already up to date", warehouse);
            }
        } catch (Exception e) {
            return new ResponseObject("500", "Failed to update warehouse", null);
        }
    }

    public ResponseObject deleteWarehouseById(int id) {
        try {
            if(!warehouseRepository.existsById(id)){
                return new ResponseObject("404", "Warehouse not found", null);
            }
            // need to check if warehouse is empty -> zone has no shipments (has no products) -> can delete warehouse
            warehouseRepository.deleteById(id);
            return new ResponseObject("200", "Warehouse deleted successfully", null);
        } catch (Exception e) {
            return new ResponseObject("500", "Failed to delete warehouse", null);
        }
    }

}
