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

    //localhost:8080/inventory/product/1?page=1
    @GetMapping("/product/{warehouseId}")
    public ResponseEntity<?> getInventoryByWarehouseId(
            @PathVariable("warehouseId") int warehouseId,
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        int limit = 20;
        page = page - 1;
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouseId(warehouseId, page, limit));
    }

    //localhost:8080/inventory/total-product/1
    @GetMapping("/total-product/{warehouseId}")
    public ResponseEntity<?> getTotalProductByWarehouseId(@PathVariable("warehouseId") int warehouseId) {
        return ResponseEntity.ok(inventoryService.getTotalProductByWarehouseId(warehouseId));
    }
}
