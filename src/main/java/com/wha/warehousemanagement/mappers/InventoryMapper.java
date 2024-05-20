package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.InventoryResponse;
import com.wha.warehousemanagement.models.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper extends EntityMapper<InventoryResponse, Inventory>{
}
