package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.OrderDetailDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.OrderDetailRepository;
import com.wha.warehousemanagement.repositories.OrderRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ResponseObject<Object> addOrderDetail(OrderDetailDTO orderDetailDTO) {
        try {
            if (orderDetailDTO.getOrderId() == null) {
                throw new CustomException(ErrorCode.ORDER_ID_BLANK);
            } else if (orderDetailDTO.getProductId() == null) {
                throw new CustomException(ErrorCode.PRODUCT_ID_BLANK);
            } else if (orderDetailDTO.getQuantity() < 0) {
                throw new CustomException(ErrorCode.QUANTITY_INVALID);
            }
            Optional<Order> order = orderRepository.getOrderById(orderDetailDTO.getOrderId());
            if (order.isEmpty()) {
                throw new CustomException(ErrorCode.ORDER_INVALID);
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order.get());
            Optional<Product> product = productRepository.getProductById(orderDetailDTO.getProductId());
            if (product.isEmpty()) {
                throw new CustomException(ErrorCode.PRODUCT_ID_BLANK);
            }
            orderDetail.setProduct(product.get());
            orderDetail.setQuantity(orderDetailDTO.getQuantity());
            orderDetailRepository.save(orderDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Order detail added successfully", orderDetail);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Order detail added unsuccessfully", null);
        }
    }

    public ResponseObject<List<OrderDetail>> getAllOrderDetails() {
        try {
            List<OrderDetail> list = new ArrayList<>(orderDetailRepository.findAll());
            if (!list.isEmpty()) {
                return new ResponseObject<>(HttpStatus.OK.value(), "Get all order details successfully", list);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order detail in db", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch order details", null);
        }
    }

    public ResponseObject<OrderDetail> getOrderDetailById(int id) {
        try {
            Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
            return orderDetail.map(detail -> new ResponseObject<>(HttpStatus.OK.value(), "Get order detail by id successfully", detail)).orElseGet(() -> new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Order detail not found", null));
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch order detail", null);
        }
    }

    public ResponseObject<OrderDetail> updateOrderDetail(int id, OrderDetailDTO orderDetailDTO) {
        try {
            Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
            if (orderDetail.isPresent()) {
                Optional<Order> order = orderRepository.getOrderById(orderDetailDTO.getOrderId());
                if (order.isEmpty()) {
                    throw new CustomException(ErrorCode.ORDER_INVALID);
                }
                Optional<Product> product = productRepository.getProductById(orderDetailDTO.getProductId());
                if (product.isEmpty()) {
                    throw new CustomException(ErrorCode.PRODUCT_ID_BLANK);
                }
                OrderDetail orderDetail1 = orderDetail.get();
                orderDetail1.setOrder(order.get());
                orderDetail1.setProduct(product.get());
                orderDetail1.setQuantity(orderDetailDTO.getQuantity());
                orderDetailRepository.save(orderDetail1);
                return new ResponseObject<>(HttpStatus.OK.value(), "Updated order detail successfully", orderDetail1);
            } else {
                return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Not found", null);
            }
        }catch (Exception e){
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update order detail", null);
        }
    }

    public ResponseObject<OrderDetail> deleteOrderDetailById(int id) {
        try {
            Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
            if (orderDetail.isPresent()) {
                orderDetailRepository.delete(orderDetail.get());
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted order detail successfully", orderDetail.get());
            } else {
                return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Not found", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete order detail", null);
        }
    }

    public ResponseObject<Object> deleteAllOrderDetails() {
        try {
            List<OrderDetail> list = new ArrayList<>(orderDetailRepository.findAll());
            if (!list.isEmpty()) {
                orderDetailRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all order details successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order detail in db", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete order details", null);
        }
    }
}
