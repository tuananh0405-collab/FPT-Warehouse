package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ProviderRequest;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.addProvider(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable("id") int id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProvider(@PathVariable("id") int id, @RequestBody ProviderRequest request) {
        return ResponseEntity.ok(providerService.updateProvider(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProvider(@PathVariable("id") int id) {
        return ResponseEntity.ok(providerService.deleteProviderById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllProviders() {
        return ResponseEntity.ok(providerService.deleteAllProviders());
    }

}
