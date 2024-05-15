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
    public ResponseEntity<ResponseObject<OrderResponse>> addOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.addOrder(request));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<OrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<OrderResponse>> getOrderById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<OrderResponse>> updateOrder(@PathVariable("id") int id, @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
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

