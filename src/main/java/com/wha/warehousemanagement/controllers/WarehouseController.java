package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.Warehouse;
import com.wha.warehousemanagement.services.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping("/all")
    public ResponseEntity<ResponseObject<List<WarehouseDTO>>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<WarehouseDTO>> getWarehouseById(@PathVariable("id") int id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject<Warehouse>> addWarehouse(@RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.addWarehouse(warehouseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Warehouse>> updateWarehouse(@PathVariable("id") int id, @RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.updateWarehouseById(id, warehouseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> deleteWarehouse(@PathVariable("id") int id) {
        return ResponseEntity.ok(warehouseService.deleteWarehouseById(id));
    }

}
