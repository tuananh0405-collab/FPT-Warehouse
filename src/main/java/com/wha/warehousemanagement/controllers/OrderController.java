package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.models.Order;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject<Order>> addOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.addOrder(orderDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<OrderDTO>>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<OrderDTO>> getOrderById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Order>> updateOrder(@PathVariable("id") int id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> deleteOrder(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.deleteOrderById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject<Object>> deleteAllOrders() {
        return ResponseEntity.ok(orderService.deleteAllOrders());
    }
}

