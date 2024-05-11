package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.models.Order;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ResponseObject addOrder(OrderDTO orderDTO) {
        if (orderDTO.getCustomerName() == null) {
            return new ResponseObject("400", "Name is blank", null);
        } else if (orderDTO.getQuantity()<0) {
            return new ResponseObject("400", "Quantity must be greater than 0", null);
        }
        Order order = new Order();
        order.setCustomerName(orderDTO.getCustomerName());
        order.setDescription(orderDTO.getDescription());
        order.setQuantity(orderDTO.getQuantity());
        order.setStatus(orderDTO.getStatus());
        order.setOrderDate(orderDTO.getOrderDate());
        order.setCountry(orderDTO.getCountry());
        orderRepository.save(order);
        return new ResponseObject("200", "Order added successfully",orderDTO);
    }

    public ResponseObject getAllOrders() {
        List<Order> list = new ArrayList<>(orderRepository.findAll());
        return new ResponseObject("200", "Get all orders successfully", list);
    }

    public ResponseObject getOrderById(int id) {
        Optional<OrderDTO> category = orderRepository.getOrderDTOById(id);
        return category.map(
                        value -> new ResponseObject("200", "Get order successfully", value))
                .orElseGet(() -> new ResponseObject("500", "Not found", null));
    }

    public ResponseObject updateOrder(int id, OrderDTO orderDTO) {
        Optional<Order> order = orderRepository.getOrderById(id);
        if (order.isPresent()) {
            Order order1 = order.get();
            order1.setCustomerName(orderDTO.getCustomerName());
            order1.setDescription(orderDTO.getDescription());
            order1.setQuantity(orderDTO.getQuantity());
            order1.setStatus(orderDTO.getStatus());
            order1.setOrderDate(orderDTO.getOrderDate());
            order1.setCountry(orderDTO.getCountry());
            return new ResponseObject("200", "Updated order successfully", orderRepository.save(order1));
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteOrderById(int id) {
        Optional<Order> order = orderRepository.getOrderById(id);
        if (order.isPresent()) {

//            Need to delete order detail first

            orderRepository.delete(order.get());
            return new ResponseObject("200", "Deleted order successfully", order.get());
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteAllOrders() {
        List<Order> list = new ArrayList<>(orderRepository.findAll());
        if (!list.isEmpty()) {
            orderRepository.deleteAll();
            return new ResponseObject("200", "Deleted orders successfully", null);
        } else {
            return new ResponseObject("500", "No category in db", null);
        }

    }
}
