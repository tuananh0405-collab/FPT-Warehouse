package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.ProviderDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provider")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }
    @PostMapping
    public ResponseEntity<ResponseObject<ProviderDTO>> addCategory(@RequestBody ProviderDTO ProviderDTO) {
        return ResponseEntity.ok(providerService.addProvider(ProviderDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<ProviderDTO>>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<ProviderDTO>> getProviderById(@PathVariable("id") int id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<ProviderDTO>> updateProvider(@PathVariable("id") int id, @RequestBody ProviderDTO providerDTO) {
        return ResponseEntity.ok(providerService.updateProvider(id, providerDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> deleteProvider(@PathVariable("id") int id) {
        return ResponseEntity.ok(providerService.deleteProviderById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject<Object>> deleteAllProviders() {
        return ResponseEntity.ok(providerService.deleteAllProviders());
    }

}
