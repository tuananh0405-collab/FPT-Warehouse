package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.OrderDetailDTO;
import com.wha.warehousemanagement.models.OrderDetail;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("order-detail")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @PostMapping
    public ResponseEntity<ResponseObject<Object>> addOrderDetail(@RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok(orderDetailService.addOrderDetail(orderDetailDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<OrderDetail>>> getAllOrderDetails() {
        return ResponseEntity.ok(orderDetailService.getAllOrderDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<OrderDetail>> getOrderDetailById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<OrderDetail>> updateOrderDetail(@PathVariable("id") int id, @RequestBody OrderDetailDTO orderDetailDTO) {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(id, orderDetailDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<OrderDetail>> deleteOrderDetailById(@PathVariable("id") int id) {
        return ResponseEntity.ok(orderDetailService.deleteOrderDetailById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject<Object>> deleteAllOrderDetails() {
        return ResponseEntity.ok(orderDetailService.deleteAllOrderDetails());
    }

}
