package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.OrderDetailDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.OrderDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("order-detail")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok(orderDetailService.addOrderDetail(orderDetailDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllOrderDetails() {
        return ResponseEntity.ok(orderDetailService.getAllOrderDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getOrderDetailById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateOrderDetail(@PathVariable("id") int id, @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(id, orderDetailDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteOrderDetailById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderDetailService.deleteOrderDetailById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject> deleteAllOrderDetails() {
        return ResponseEntity.ok(orderDetailService.deleteAllOrderDetails());
    }

}
