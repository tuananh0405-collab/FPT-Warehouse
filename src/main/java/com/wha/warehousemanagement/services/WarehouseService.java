package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.WarehouseMapper;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public ResponseObject<?> getAllWarehouses() {
        try {
            List<WarehouseDTO> warehouses = warehouseRepository.findAll()
                    .stream()
                    .map(warehouseMapper::toDto)
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouses fetched successfully", warehouses);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch warehouses", null);
        }
    }

    public ResponseObject<?> getWarehouseById(int id) {
        try {
            WarehouseDTO warehouseDTO = warehouseRepository.findById(id).map(warehouseMapper::toDto)
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)
                    );
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse fetched successfully", warehouseDTO);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch warehouse", null);
        }
    }

    public ResponseObject<?> addWarehouse(WarehouseDTO warehouseDTO) {
        try {
            if (warehouseRepository.existsByName(warehouseDTO.getName())) {
                throw new CustomException(ErrorCode.WAREHOUSE_ALREADY_EXISTS);
            } else if (warehouseRepository.existsByAddress(warehouseDTO.getAddress())) {
                throw new CustomException(ErrorCode.WAREHOUSE_ADDRESS_ALREADY_EXISTS);
            }
            Warehouse warehouse = new Warehouse();
            warehouse.setName(warehouseDTO.getName());
            warehouse.setDescription(warehouseDTO.getDescription());
            warehouse.setCreatedAt(new Date());
            warehouse.setAddress(warehouseDTO.getAddress());
            System.out.println(warehouse);
            warehouseRepository.save(warehouse);
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse added successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add warehouse", null);
        }
    }

    public ResponseObject<?> updateWarehouseById(int id, WarehouseDTO warehouseDTO) {
        try {
            Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND));
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
                return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse updated successfully", warehouse);
            } else {
                return new ResponseObject<>(HttpStatus.OK.value(), "No changes made", warehouse);
            }
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update warehouse", null);
        }
    }

    public ResponseObject<Object> deleteWarehouseById(int id) {
        try {
            if (!warehouseRepository.existsById(id)) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
            }
            // need to check if warehouse is empty
            // -> zone has no shipments (has no products)
            // -> can delete warehouse
            warehouseRepository.deleteById(id);
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouse deleted successfully", null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete warehouse", null);
        }
    }

}
