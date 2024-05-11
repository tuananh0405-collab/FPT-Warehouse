package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.repositories.WarehouseRepository;

public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

}
