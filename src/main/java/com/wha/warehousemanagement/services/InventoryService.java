package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.PageResponse;
import com.wha.warehousemanagement.dtos.requests.InventoryRequest;
import com.wha.warehousemanagement.dtos.responses.InventoryResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.InventoryMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.Inventory;
import com.wha.warehousemanagement.models.Product;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Zone;
import com.wha.warehousemanagement.repositories.InventoryRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import com.wha.warehousemanagement.repositories.SearchRepository;
import com.wha.warehousemanagement.repositories.ZoneRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final SearchRepository searchRepository;

    public ResponseObject<?> getAllInventories() {
        try{
            List<InventoryResponse> response =
                inventoryRepository.findAll()
                    .stream()
                    .map(imp -> {
                        InventoryResponse inventoryResponse = inventoryMapper.toDto(imp);
                        InventoryResponse.builder()
                                .product(productMapper.toDto(imp.getProduct()))
                                .zoneName(imp.getZone().getName()).quantity(0).build();
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

//    public ResponseObject<?> getInventoryById(Integer id) {
//        try {
//            InventoryResponse response = inventoryRepository.findById(id)
//                .map(imp -> {
//                    InventoryResponse inventoryResponse = inventoryMapper.toDto(imp);
//                    InventoryResponse.builder()
//                            .product(productMapper.toDto(imp.getProduct()))
//                            .zoneName(imp.getZone().getName()).quantity().build();
//                    return inventoryResponse;
//                })
//                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
//            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory retrieved successfully", response);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get inventory", null);
//        }
//    }

    public ResponseObject<?> getInventoryById(Integer id) {
        try {
            InventoryResponse response = inventoryRepository.findById(id)
                    .map(imp -> {
                        InventoryResponse inventoryResponse = inventoryMapper.toDto(imp);
                        return InventoryResponse.builder()
                                .product(productMapper.toDto(imp.getProduct()))
                                .zoneName(imp.getZone().getName())
                                .quantity(imp.getQuantity()) // Cung cấp giá trị quantity
                                .build();
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
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventory added successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add inventory", null);
        }
    }

    public ResponseObject<?> getInventoryByWarehouseId(
           int pageNo, int limit, String sortBy, int warehouseId, String... search
    ) {
        try {
            return new ResponseObject<>(HttpStatus.OK.value(), "Inventories retrieved successfully", searchRepository.searchInventories(pageNo, limit, sortBy,warehouseId, search));
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all inventories", null);
        }
    }

    public ResponseObject<?> getTotalProductByWarehouseIdFilter(int warehouseId, Integer categoryId, String zoneName) {
        try {
            Long totalProduct = inventoryRepository.countInventoriesByWarehouseIdAndCategoryId(warehouseId, categoryId, zoneName);
            return new ResponseObject<>(HttpStatus.OK.value(), "Total product retrieved successfully", totalProduct);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get total product", null);
        }
    }


    public ResponseObject<?> getTotalProductByWarehouseId(int warehouseId) {
        try {
            Long totalProduct = inventoryRepository.countInventoriesByWarehouseId(warehouseId);
            return new ResponseObject<>(HttpStatus.OK.value(), "Total product retrieved successfully", totalProduct);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get total product", null);
        }
    }


    //zones transfer
    @Transactional
    public void transferProductBetweenZones(int productId, int fromZoneId, int toZoneId, int quantity) {
        // Giảm số lượng sản phẩm trong zone xuất phát
        Inventory fromInventory = inventoryRepository.findByProductIdAndZoneId(productId, fromZoneId);
        if (fromInventory == null || fromInventory.getQuantity() < quantity) {
            throw new RuntimeException("Không đủ số lượng sản phẩm để chuyển");
        }
        fromInventory.setQuantity(fromInventory.getQuantity() - quantity);
        inventoryRepository.save(fromInventory);

        // Tăng số lượng sản phẩm trong zone đích
        Inventory toInventory = inventoryRepository.findByProductIdAndZoneId(productId, toZoneId);
        if (toInventory == null) {
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
            Zone zone = zoneRepository.findById(toZoneId).orElseThrow(() -> new RuntimeException("Zone not found"));

            toInventory = new Inventory();
            toInventory.setProduct(product);
            toInventory.setZone(zone);
            toInventory.setQuantity(0);
        }
        toInventory.setQuantity(toInventory.getQuantity() + quantity);
        inventoryRepository.save(toInventory);
    }
    //

}
