package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.OrderDetailDTO;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.OrderDetailRepository;
import com.wha.warehousemanagement.repositories.OrderRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public ResponseObject addOrderDetail(OrderDetailDTO orderDetailDTO) {
        if (orderDetailDTO.getOrderId() == null) {
            return new ResponseObject("400", "Order id is blank", null);
        } else if (orderDetailDTO.getProductId() == null) {
            return new ResponseObject("400", "Product id is blank", null);
        } else if (orderDetailDTO.getQuantity()<0) {
            return new ResponseObject("400", "Quantity must be greater than 0", null);
        }
        Optional<Order> order = orderRepository.getOrderById(orderDetailDTO.getOrderId());
        if (order.isEmpty()) {
            return new ResponseObject("400", "Order invalid", null);
        }
        Optional<Product> product = productRepository.getProductById(orderDetailDTO.getProductId());
        if (product.isEmpty()) {
            return new ResponseObject("400", "Product invalid", null);
        }
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order.get());
        orderDetail.setProduct(product.get());
        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetailRepository.save(orderDetail);
        return new ResponseObject("200", "Product added successfully", orderDetail);
    }

    public ResponseObject getAllOrderDetails() {
        List<OrderDetail> list = new ArrayList<>(orderDetailRepository.findAll());
        return new ResponseObject("200", "Get all order details successfully", list);
    }

    public ResponseObject getOrderDetailById(int id) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
        return orderDetail.map(
                        value -> new ResponseObject("200", "Get order detail successfully", value))
                .orElseGet(() -> new ResponseObject("500", "Not found", null));
    }

    public ResponseObject updateOrderDetail(int id, OrderDetailDTO orderDetailDTO) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
        if (orderDetail.isPresent()) {
            Optional<Order> order = orderRepository.getOrderById(orderDetailDTO.getOrderId());
            if (order.isEmpty()) {
                return new ResponseObject("400", "Order invalid", null);
            }
            Optional<Product> product = productRepository.getProductById(orderDetailDTO.getProductId());
            if (product.isEmpty()) {
                return new ResponseObject("400", "Product invalid", null);
            }
            OrderDetail orderDetail1 = orderDetail.get();
            orderDetail1.setOrder(order.get());
            orderDetail1.setProduct(product.get());
            orderDetail1.setQuantity(orderDetailDTO.getQuantity());
            return new ResponseObject("200", "Updated order detail successfully", orderDetailRepository.save(orderDetail1));
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteOrderDetailById(int id) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
        if (orderDetail.isPresent()) {
            orderDetailRepository.delete(orderDetail.get());
            return new ResponseObject("200", "Deleted order detail successfully", orderDetail.get());
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteAllOrderDetails() {
        List<OrderDetail> list = new ArrayList<>(orderDetailRepository.findAll());
        if (!list.isEmpty()) {
            orderDetailRepository.deleteAll();
            return new ResponseObject("200", "Deleted order details successfully", null);
        } else {
            return new ResponseObject("500", "No product in db", null);
        }

    }
}
