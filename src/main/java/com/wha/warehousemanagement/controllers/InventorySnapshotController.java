package com.wha.warehousemanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.services.InventorySnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/inventory-snapshot")
@RequiredArgsConstructor
public class InventorySnapshotController {
    private final InventorySnapshotService inventorySnapshotService;

    @GetMapping("/report")
    public ResponseEntity<Resource> getInventoryReports(@RequestParam("year") int year,
                                                        @RequestParam("month") int month) throws IOException, InterruptedException {
        List<InventorySnapshotResponse> reports = inventorySnapshotService.getSnapshotsByMonth(year, month);

        if (reports.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        ObjectMapper mapper = new ObjectMapper();
        File tempFile = File.createTempFile("inventory_snapshot_report", ".json");
        mapper.writeValue(tempFile, reports);

        String outputExcelFile = tempFile.getAbsolutePath().replace(".json", ".xlsx");
        ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/python-scripts/generate_excel_report.py", tempFile.getAbsolutePath(), outputExcelFile);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Error generating Excel report");
        }

        File excelFile = new File(outputExcelFile);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping("/create-snapshots")
    public ResponseEntity<String> createSnapshotsForPeriod(@RequestParam("startYear") int startYear,
                                                           @RequestParam("startMonth") int startMonth,
                                                           @RequestParam("endYear") int endYear,
                                                           @RequestParam("endMonth") int endMonth) {
        inventorySnapshotService.createSnapshotsForPeriod(startYear, startMonth, endYear, endMonth);
        return ResponseEntity.ok("Snapshots created successfully for the given period.");
    }
}
