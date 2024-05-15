package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.OrderResponse;
import com.wha.warehousemanagement.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper extends EntityMapper<OrderResponse, Order>{
}
