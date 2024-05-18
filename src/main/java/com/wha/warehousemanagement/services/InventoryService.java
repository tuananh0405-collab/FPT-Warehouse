package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.InventoryRequest;
import com.wha.warehousemanagement.dtos.responses.InventoryResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.InventoryMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.InventoryRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import com.wha.warehousemanagement.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;

    public ResponseObject<?> getAllInventories() {
        try{
            List<InventoryResponse> response =
                inventoryRepository.findAll()
                    .stream()
                    .map(imp -> {
                        InventoryResponse inventoryResponse = inventoryMapper.toDto(imp);
                        inventoryResponse.setProduct(productMapper.toDto(imp.getProduct()));
                        inventoryResponse.setZoneName(imp.getZone().getName());
                        return inventoryResponse;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all inventories", null);
        }
    }

    public ResponseObject<?> getInventoryById(Integer id) {
        try {
            InventoryResponse response = inventoryRepository.findById(id)
                .map(imp -> {
                    InventoryResponse inventoryResponse = inventoryMapper.toDto(imp);
                    inventoryResponse.setProduct(productMapper.toDto(imp.getProduct()));
                    inventoryResponse.setZoneName(imp.getZone().getName());
                    return inventoryResponse;
                })
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventory", null);
        }
    }

    public ResponseObject<?> deleteInventoryById(Integer id) {
        try {
            inventoryRepository.deleteById(id);
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory deleted successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete inventory", null);
        }
    }

    public ResponseObject<?> updateInventory(int id, InventoryRequest request) {
        try {
            inventoryRepository.findById(id)
                .map(imp -> {
                    imp.setProduct(productRepository.findById(request.getProductId()).orElse(null));
                    imp.setQuantity(request.getQuantity());
                    imp.setExpiredAt(request.getExpiredAt());
                    imp.setZone(zoneRepository.findById(request.getZoneId()).orElse(null));
                    return inventoryRepository.save(imp);
                })
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory updated successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update inventory", null);
        }
    }

    public ResponseObject<?> addInventory(int id, InventoryRequest request) {
        try {
           //Handle case Zone is full,
            //Handle case consolidate goods
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory added successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add inventory", null);
        }
    }
}
