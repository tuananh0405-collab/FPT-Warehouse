package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseObject> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getWarehouseById(@PathVariable("id") int id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addWarehouse(@RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.addWarehouse(warehouseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateWarehouse(@PathVariable("id") int id, @RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.updateWarehouseById(id, warehouseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteWarehouse(@PathVariable("id") int id) {
        return ResponseEntity.ok(warehouseService.deleteWarehouseById(id));
    }

}
