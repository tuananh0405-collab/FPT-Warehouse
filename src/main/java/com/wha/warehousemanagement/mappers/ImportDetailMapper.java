package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ImportDetailResponse;
import com.wha.warehousemanagement.models.ImportDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImportDetailMapper extends EntityMapper<ImportDetailResponse, ImportDetail>{
}
