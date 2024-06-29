package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.models.InventorySnapshot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventorySnapshotMapper extends EntityMapper<InventorySnapshotResponse, InventorySnapshot> {
}
