package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.requests.OrderRequest;
import com.wha.warehousemanagement.dtos.responses.OrderResponse;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> addOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.addOrder(request));
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") int id, @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.deleteOrderById(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllOrders() {
        return ResponseEntity.ok(orderService.deleteAllOrders());
    }
}

