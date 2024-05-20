package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.ExportDetailResponse;
import com.wha.warehousemanagement.models.ExportDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExportDetailMapper extends EntityMapper<ExportDetailResponse, ExportDetail>{
}
