package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ProviderResponse;
import com.wha.warehousemanagement.models.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProviderMapper extends EntityMapper<ProviderResponse, Provider> {
}
