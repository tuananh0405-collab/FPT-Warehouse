package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.InventoryRequest;
import com.wha.warehousemanagement.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable("id") int id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInventory(@PathVariable("id") int id, @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addInventory(@RequestBody InventoryRequest request, @PathVariable int id) {
        return ResponseEntity.ok(inventoryService.addInventory(id, request));
    }

    //localhost:6060/inventory/products/?page=1&sortBy=id&search=product:1
    @GetMapping("/products/")
    public ResponseEntity<?> getInventoryByWarehouseId(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sortBy", defaultValue = "id:asc") String sortBy,
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) String... search
    ) {
        int limit = 20;
        page = page - 1;
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouseId(page, limit, sortBy, warehouseId, search));
    }

    //localhost:6060/inventory/total-product-filter/1?categoryId=1&zoneName=A1
    @GetMapping("/total-product-filter/{warehouseId}")
    public ResponseEntity<?> getTotalProductByWarehouseIdFilter(
            @PathVariable("warehouseId") int warehouseId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "zoneName", required = false) String zoneName
    ) {
        if (categoryId != null && categoryId == 0) {
            categoryId = null;
        }
        if (zoneName != null && zoneName.isBlank()) {
            zoneName = null;
        }
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseIdFilter(warehouseId, categoryId, zoneName));
    }

    //localhost:8080/inventory/total-product/1
    @GetMapping("/total-product/{warehouseId}")
    public ResponseEntity<?> getTotalProductByWarehouseId(@PathVariable("warehouseId") int warehouseId) {
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseId(warehouseId));
    }

    //zones transfer
    @PostMapping("/transfer")
    public ResponseEntity<String> transferProduct(@RequestParam int productId,
                                                  @RequestParam int fromZoneId,
                                                  @RequestParam int toZoneId,
                                                  @RequestParam int quantity) {
        try {
            inventoryService.transferProductBetweenZones(productId, fromZoneId, toZoneId, quantity);
            return ResponseEntity.ok("Chuyển sản phẩm thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Sửa lại URL để tránh xung đột
    @GetMapping("/products-with-filters/")
    public ResponseEntity<?> getInventoryByWarehouseIdWithFilters(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String zoneName,
            @RequestParam(required = false) Integer quantityLow,
            @RequestParam(required = false) Integer quantityHigh
    ) {
        int limit = 20;
        page = page - 1;
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouseIdWithFilters(
                page, limit, warehouseId, productName, categoryId, zoneName, quantityLow, quantityHigh));
    }

    @GetMapping("/total-product-with-filters/")
    public ResponseEntity<?> getTotalProductByWarehouseIdWithFilters(
            @RequestParam(value = "warehouseId") int warehouseId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "zoneName", required = false) String zoneName,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "quantityLow", required = false) Integer quantityLow,
            @RequestParam(value = "quantityHigh", required = false) Integer quantityHigh
    ) {
        if (categoryId != null && categoryId == 0) {
            categoryId = null;
        }
        if (zoneName != null && zoneName.isBlank()) {
            zoneName = null;
        }
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseIdWithFilters(
                warehouseId, categoryId, zoneName, productName, quantityLow, quantityHigh));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<?> getInventoriesByWarehouseIdWithFilters(
            @PathVariable("warehouseId") Integer warehouseId,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("includeExpired") boolean includeExpired,
            @RequestParam("includeNearExpired") boolean includeNearExpired,
            @RequestParam("includeValid") boolean includeValid) {
        return ResponseEntity.ok(inventoryService.getInventoriesByWarehouseIdWithFilters(
                warehouseId, pageNo, pageSize, includeExpired, includeNearExpired, includeValid));
    }
}
