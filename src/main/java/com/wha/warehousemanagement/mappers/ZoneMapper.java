package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ZoneResponse;
import com.wha.warehousemanagement.models.Zone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ZoneMapper extends EntityMapper<ZoneResponse, Zone>{
}
