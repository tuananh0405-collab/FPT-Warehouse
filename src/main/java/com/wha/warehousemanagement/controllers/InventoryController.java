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

    //localhost:6060/inventory/product/1?page=1&sortBy=id&direction=asc&categoryId=
    @GetMapping("/product/{warehouseId}")
    public ResponseEntity<?> getInventoryByWarehouseId(
            @PathVariable("warehouseId") int warehouseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "categoryId", required = false) Integer categoryId
    ) {
        int limit = 20;
        page = page - 1;
        if (categoryId == 0){
            categoryId = null;
        }
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouseId(warehouseId, page, limit, sortBy, direction, categoryId));
    }

    //localhost:8080/inventory/total-product/1
    @GetMapping("/total-product/{warehouseId}")
    public ResponseEntity<?> getTotalProductByWarehouseId(@PathVariable("warehouseId") int warehouseId) {
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseId(warehouseId));
    }
}
