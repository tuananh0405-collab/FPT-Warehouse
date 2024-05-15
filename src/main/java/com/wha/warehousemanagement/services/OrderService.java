package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.OrderRequest;
import com.wha.warehousemanagement.dtos.responses.OrderResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.OrderMapper;
import com.wha.warehousemanagement.models.Order;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;


    public ResponseObject<?> addOrder(OrderRequest request) {
        try {
            if (request.getCustomerName() == null) {
                throw new CustomException(ErrorCode.CUSTOMER_NAME_BLANK);
            } else if (request.getQuantity() < 0) {
                throw new CustomException(ErrorCode.QUANTITY_INVALID);
            }
            Order order = new Order();
            order.setCustomerName(request.getCustomerName());
            order.setDescription(request.getDescription());
            order.setQuantity(request.getQuantity());
            order.setStatus(request.getStatus());
            order.setOrderDate(request.getOrderDate());
            order.setCountry(request.getCountry());
            orderRepository.save(order);
            Optional<OrderResponse> response = getLastOrderResponse();
            if (response.isEmpty()) {
                throw new CustomException(ErrorCode.ORDER_ADD_FAILED);
            }
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Order added successfully",
                    response.get());
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Order added unsuccessfully", null);
        }
    }

    private Optional<OrderResponse> getLastOrderResponse() {
        List<OrderResponse> responses = orderRepository.getAllOrderResponses();
        if (!responses.isEmpty()) {
            return Optional.of(responses.get(0));
        } else {
            return Optional.empty();
        }
    }

    public ResponseObject<?> getAllOrders() {
        try {
            List<OrderResponse> list = orderRepository.findAll()
                    .stream()
                    .map(orderMapper::toDto)
                    .toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all orders successfully", list);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order in db", null);
        }
    }

    public ResponseObject<?> getOrderById(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        OrderResponse response = orderMapper.toDto(order);
        return new ResponseObject<>(HttpStatus.OK.value(), "Get category by id successfully", response);
    }

    public ResponseObject<?> updateOrder(int id, OrderRequest request) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
            if (request.getCustomerName() != null &&
                    !request.getCustomerName().trim().isEmpty() &&
                    !request.getCustomerName().equals(order.getCustomerName()) &&
                    orderRepository.existsByCustomerName(request.getCustomerName())) {
                throw new CustomException(ErrorCode.ORDER_ALREADY_EXISTS);
            }
            if (request.getCustomerName() != null) {
                order.setCustomerName(request.getCustomerName());
            }
            if (request.getDescription() != null) {
                order.setDescription(request.getDescription());
            }
            if (request.getQuantity() != null) {
                order.setQuantity(request.getQuantity());
            }
            if (request.getStatus() != null) {
                order.setStatus(request.getStatus());
            }
            if (request.getOrderDate() != null) {
                order.setOrderDate(request.getOrderDate());
            }
            if (request.getCountry() != null) {
                order.setCountry(request.getCountry());
            }
            orderRepository.save(order);
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Updated category successfully",
                    orderMapper.toDto(order));
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update category", null);
        }
    }

    public ResponseObject<?> deleteOrderById(int id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
            orderRepository.delete(order);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted order successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete order", null);
        }
    }

    public ResponseObject<?> deleteAllOrders() {
        try {
            List<Order> list = orderRepository.findAll();
            if (!list.isEmpty()) {
                orderRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all orders successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No orders to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete orders", null);
        }
    }
}
