package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ImportRequest;
import com.wha.warehousemanagement.services.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping
    public ResponseEntity<?> addImport(@RequestBody ImportRequest request) {
        return ResponseEntity.ok(importService.addImport(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllImports() {
        return ResponseEntity.ok(importService.getAllImports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllImports(@PathVariable("id") int id) {
        return ResponseEntity.ok(importService.getImportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImport(@PathVariable("id") int id, @RequestBody ImportRequest request) {
        return ResponseEntity.ok(importService.updateImport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImportById(@PathVariable("id") int id) {
        return ResponseEntity.ok(importService.deleteImportById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllImports() {
        return ResponseEntity.ok(importService.deleteAllImports());
    }

    //localhost:8080/import/import?page=1
    @GetMapping("/import")
    public ResponseEntity<?> getAllImport(
            @RequestParam(value = "page", defaultValue = "1") int page
    ) {
        int limit = 20;
        page = page - 1;
        return ResponseEntity.ok(importService.getAllImports(page, limit));
    }

    //localhost:8080/import/total-import
    @GetMapping("/total-import")
    public ResponseEntity<?> getTotalImport() {
        return ResponseEntity.ok(importService.getTotalImports());
    }
}
