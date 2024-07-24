package com.wha.warehousemanagement.models;

public class ExportNotification {

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

    // Getters and setters
    public Integer getExportId() {
        return exportId;
    }

    public void setExportId(Integer exportId) {
        this.exportId = exportId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}