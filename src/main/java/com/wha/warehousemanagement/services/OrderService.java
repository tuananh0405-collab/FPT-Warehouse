package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.models.Order;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ResponseObject getAllOrders(){
        List<Order> list = orderRepository.findAll();
        return new ResponseObject("200", "Get all orders successfully", list);
    }
}
