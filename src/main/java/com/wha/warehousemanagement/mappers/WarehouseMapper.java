package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper extends EntityMapper<WarehouseDTO, Warehouse>{
}
