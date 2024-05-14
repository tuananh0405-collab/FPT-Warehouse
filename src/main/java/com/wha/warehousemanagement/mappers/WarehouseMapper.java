package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WarehouseMapper extends EntityMapper<WarehouseDTO, Warehouse>{
    WarehouseMapper INSTANCE = Mappers.getMapper(WarehouseMapper.class);
}
