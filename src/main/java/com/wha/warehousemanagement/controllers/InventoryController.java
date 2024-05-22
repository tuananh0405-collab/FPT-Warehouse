package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.InventoryRequest;
import com.wha.warehousemanagement.services.InventoryService;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouseId(page, limit, sortBy,warehouseId, search));
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
        if (zoneName != null && zoneName.isBlank()){
            zoneName = null;
        }
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseIdFilter(warehouseId, categoryId, zoneName));
    }

    //localhost:8080/inventory/total-product/1
    @GetMapping("/total-product/{warehouseId}")
    public ResponseEntity<?> getTotalProductByWarehouseId(@PathVariable("warehouseId") int warehouseId) {
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseId(warehouseId));
    }
}
