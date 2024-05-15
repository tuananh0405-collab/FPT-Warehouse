package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.responses.OrderResponse;
import com.wha.warehousemanagement.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    OrderResponse orderToOrderResponse(Order order);
}
