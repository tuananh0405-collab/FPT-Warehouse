package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.ExportByAdminReqRequest;
import com.wha.warehousemanagement.dtos.requests.ExportRequest;
import com.wha.warehousemanagement.dtos.requests.processExportByStaffRequest;
import com.wha.warehousemanagement.dtos.responses.ExportByAdminReqResponse;
import com.wha.warehousemanagement.models.ResponseObject;
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

    //
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

    //localhost:6060/export/search?page=1&sortBy=id&exportDate=2021-09-01&customerName=customer&customerAddress=address&status=COMPLETED
//    @GetMapping("/exportDetails/search")
//    public ResponseEntity<?> searchExportDetails(
//            @RequestParam(value = "page", defaultValue = "1") int page,
//            @RequestParam("warehouseId") int warehouseId,
//            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
//            @RequestParam(value = "direction", defaultValue = "asc") String direction,
//            @RequestParam(value = "exportDate", required = false) String exportDate,
//            @RequestParam(value = "customerName", required = false) String customerName,
//            @RequestParam(value = "customerAddress", required = false) String customerAddress,
//            @RequestParam(value = "status", required = false) String status
//    ) {
//        int limit = 20;
//        page = page - 1;
//        return ResponseEntity.ok(exportService.searchExportDetails(
//                page, limit, sortBy,direction,warehouseId, exportDate, customerName, customerAddress, status
//        ));
//    }

    // For Admin create export request in warehouse A and import request for warehouse B (only product and quantity)
    @PostMapping("/admin/req-transfer")
    public ResponseEntity<ResponseObject<ExportByAdminReqResponse>> createExportRequest(@RequestBody ExportByAdminReqRequest request) {
        return ResponseEntity.ok(exportService.createTransferBetweenWarehouses(request));
    }

    @PostMapping("/staff/process-export")
    public ResponseEntity<?> processExportByStaff(@RequestBody processExportByStaffRequest request) {
        return ResponseEntity.ok(exportService.processExportRequestToTransfer(request));
    }
}
