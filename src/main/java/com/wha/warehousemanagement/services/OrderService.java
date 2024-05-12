package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.dtos.ProductOrderDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.Order;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.OrderRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public ResponseObject<Order> addOrder(OrderDTO orderDTO) {
        try {
            if (orderDTO.getCustomerName() == null) {
                throw new CustomException(ErrorCode.CUSTOMER_NAME_BLANK);
            } else if (orderDTO.getQuantity() < 0) {
                throw new CustomException(ErrorCode.QUANTITY_INVALID);
            }
            Order order = new Order(
                    orderDTO.getCustomerName(),
                    orderDTO.getDescription(),
                    orderDTO.getQuantity(),
                    orderDTO.getStatus(),
                    orderDTO.getOrderDate(),
                    orderDTO.getCountry()
            );
            orderRepository.save(order);
            return new ResponseObject<>(HttpStatus.OK.value(), "Order added successfully", order);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Order added unsuccessfully", null);
        }
    }

    public ResponseObject<List<OrderDTO>> getAllOrders() {
        try {
            List<OrderDTO> list = new ArrayList<>();
            orderRepository.findAll().forEach(order -> {
                OrderDTO orderDTO = new OrderDTO(order.getId(), order.getCustomerName(), order.getDescription(),
                        order.getQuantity(), order.getStatus(), order.getOrderDate(), order.getCountry(),
                        productRepository.findProductOrderByOrderId(order.getId()));
                list.add(orderDTO);
            });
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all orders successfully", list);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order in db", null);
        }
    }

    public ResponseObject<OrderDTO> getOrderById(int id) {
        try {
            Optional<OrderDTO> orderDTO = orderRepository.getOrderDTOById(id);
            if (orderDTO.isEmpty()) {
                throw new CustomException(ErrorCode.ORDER_INVALID);
            }
            List<ProductOrderDTO> productDetails = productRepository.findProductOrderByOrderId(id);
            orderDTO.get().setProducts(productDetails);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get order by id successfully", orderDTO.get());
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", null);
        }
    }

    public ResponseObject<Order> updateOrder(int id, OrderDTO orderDTO) {
        try {
            Optional<Order> order = orderRepository.getOrderById(id);
            if (order.isPresent()) {
                order.get().setCustomerName(orderDTO.getCustomerName());
                order.get().setDescription(orderDTO.getDescription());
                order.get().setQuantity(orderDTO.getQuantity());
                order.get().setStatus(orderDTO.getStatus());
                order.get().setOrderDate(orderDTO.getOrderDate());
                order.get().setCountry(orderDTO.getCountry());
                orderRepository.save(order.get());
                return new ResponseObject<>(HttpStatus.OK.value(), "Updated order successfully", order.get());
            } else {
                throw new CustomException(ErrorCode.ORDER_INVALID);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", null);
        }
    }

    public ResponseObject<Object> deleteOrderById(int id) {
        Optional<Order> order = orderRepository.getOrderById(id);
        if (order.isPresent()) {

            //Need to handle when order is deleted, the product order is also deleted
            // -> Product must be rolled back to the shipment or zone to be stored.

            orderRepository.delete(order.get());
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted order successfully", null);
        } else {
            throw new CustomException(ErrorCode.ORDER_INVALID);
        }
    }

    public ResponseObject<Object> deleteAllOrders() {
        List<Order> list = new ArrayList<>(orderRepository.findAll());
        if (!list.isEmpty()) {
            orderRepository.deleteAll();
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all orders successfully", null);
        } else {
            return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order in db", null);
        }
    }
}
