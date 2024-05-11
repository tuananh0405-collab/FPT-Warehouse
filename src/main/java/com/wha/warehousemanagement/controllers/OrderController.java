package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<ResponseObject> addOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.addOrder(orderDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getAllOrders(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateOrder(@PathVariable("id") int id, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteOrder(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.deleteOrderById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject> deleteAllOrders() {
        return ResponseEntity.ok(orderService.deleteAllOrders());
    }
}
