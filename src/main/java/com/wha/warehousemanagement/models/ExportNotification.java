package com.wha.warehousemanagement.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExportNotification {

    // Getters and setters
    private Integer exportId;
    private String message;

    public ExportNotification() {}

    public ExportNotification(Integer exportId, String message) {
        this.exportId = exportId;
        this.message = message;
    }

    public ExportNotification(Export export) {
        this.exportId = export.getId();
        this.message = "New export created with ID: " + export.getId() + " for warehouse: " + export.getWarehouseTo().getName();
    }
}