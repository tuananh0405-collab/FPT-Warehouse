package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ExportDetailRequest;
import com.wha.warehousemanagement.dtos.requests.ImportDetailRequest;
import com.wha.warehousemanagement.services.ExportDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/export-details")
@RequiredArgsConstructor
public class ExportDetailController {
    private final ExportDetailService exportDetailService;

    @GetMapping
    public ResponseEntity<?> getAllExportDetails() {
        return ResponseEntity.ok(exportDetailService.getAllExportDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExportDetailById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(exportDetailService.getExportDetailById(id));
    }

    @PostMapping
    public ResponseEntity<?> createExportDetail(@RequestBody List<ExportDetailRequest> requests) {
        return ResponseEntity.ok(exportDetailService.createExportDetail(requests));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExportDetail(@PathVariable("id") Integer id, @RequestBody ExportDetailRequest request) {
        return ResponseEntity.ok(exportDetailService.updateExportDetail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExportDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(exportDetailService.deleteExportDetail(id));
    }
}
