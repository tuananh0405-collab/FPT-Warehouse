package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.dtos.ProductOrderDTO;
import com.wha.warehousemanagement.dtos.requests.OrderRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.dtos.responses.OrderResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CategoryMapper;
import com.wha.warehousemanagement.mappers.OrderMapper;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.Order;
import com.wha.warehousemanagement.models.ResponseObject;
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
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public ResponseObject<OrderResponse> addOrder(OrderRequest request) {
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

    public ResponseObject<List<OrderResponse>> getAllOrders() {
        try {
            List<OrderResponse> list = new ArrayList<>();
            orderRepository.findAll().forEach(order -> {
//                OrderResponse response = new OrderResponse();
//                response.setId(order.getId());
//                response.setCustomerName(order.getCustomerName());
//                response.setDescription(response.getDescription());
//                response.setQuantity(response.getQuantity());
//                response.setStatus(response.getStatus());
//                response.setOrderDate(response.getOrderDate());
//                response.setCountry(response.getCountry());
                OrderResponse response = OrderMapper.INSTANCE.orderToOrderResponse(order);
                list.add(response);
            });
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all orders successfully", list);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order in db", null);
        }
    }

    public ResponseObject<OrderResponse> getOrderById(int id) {
//        try {
//            Optional<OrderDTO> orderDTO = orderRepository.getOrderDTOById(id);
//            if (orderDTO.isEmpty()) {
//                throw new CustomException(ErrorCode.ORDER_INVALID);
//            }
//            List<ProductOrderDTO> productDetails = productRepository.findProductOrderByOrderId(id);
//            orderDTO.get().setProducts(productDetails);
//            return new ResponseObject<>(HttpStatus.OK.value(), "Get order by id successfully", orderDTO.get());
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", null);
//        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        OrderResponse response = OrderMapper.INSTANCE.orderToOrderResponse(order);
        return new ResponseObject<>(HttpStatus.OK.value(), "Get category by id successfully", response);
    }

    public ResponseObject<OrderResponse> updateOrder(int id, OrderRequest request) {
//        try {
//            Optional<Order> order = orderRepository.getOrderById(id);
//            if (order.isPresent()) {
//                order.get().setCustomerName(orderDTO.getCustomerName());
//                order.get().setDescription(orderDTO.getDescription());
//                order.get().setQuantity(orderDTO.getQuantity());
//                order.get().setStatus(orderDTO.getStatus());
//                order.get().setOrderDate(orderDTO.getOrderDate());
//                order.get().setCountry(orderDTO.getCountry());
//                orderRepository.save(order.get());
//                return new ResponseObject<>(HttpStatus.OK.value(), "Updated order successfully", order.get());
//            } else {
//                throw new CustomException(ErrorCode.ORDER_INVALID);
//            }
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error", null);
//        }

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
                    OrderMapper.INSTANCE.orderToOrderResponse(order));
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update category", null);
        }
    }

    public ResponseObject<Object> deleteOrderById(int id) {
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

    public ResponseObject<Object> deleteAllOrders() {
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
