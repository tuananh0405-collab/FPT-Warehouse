package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ExportRequest;
import com.wha.warehousemanagement.services.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping
    public ResponseEntity<?> addExport(@RequestBody ExportRequest request) {
        return ResponseEntity.ok(exportService.addExport(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllExports() {
        return ResponseEntity.ok(exportService.getAllExports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExportById(@PathVariable("id") int id) {
        return ResponseEntity.ok(exportService.getExportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExport(@PathVariable("id") int id, @RequestBody ExportRequest request) {
        return ResponseEntity.ok(exportService.updateExport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExport(@PathVariable("id") int id) {
        return ResponseEntity.ok(exportService.deleteExportById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllExports() {
        return ResponseEntity.ok(exportService.deleteAllExports());
    }

}
