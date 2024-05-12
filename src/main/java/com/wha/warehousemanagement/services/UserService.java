package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.UserDTO;
import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.UserRepository;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final WarehouseRepository warehouseRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(WarehouseRepository warehouseRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseObject<Object> addUser(UserDTO userDTO) {
        try {
            if (userDTO.getWarehouseId() == null) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
            }
            User user = new User();
            Optional<Warehouse> warehouse = warehouseRepository.findById(userDTO.getWarehouseId());
            Optional<WarehouseDTO> warehouseDTO = warehouseRepository.getWarehouseById(userDTO.getWarehouseId());
            if (warehouse.isEmpty()) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
            }
            user.setFullName(userDTO.getFullName());
            user.setUsername(userDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setEmail(userDTO.getEmail());
            user.setPhone(userDTO.getPhone());
            user.setAddress(userDTO.getAddress());
            user.setRole(userDTO.getRole());
            user.setWarehouse(warehouse.get());
            userRepository.save(user);
            UserDTO ud = new UserDTO();
            if (warehouseDTO.isPresent()) {
                ud.setFullName(userDTO.getFullName());
                ud.setUsername(userDTO.getUsername());
                ud.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                ud.setEmail(userDTO.getEmail());
                ud.setPhone(userDTO.getPhone());
                ud.setAddress(userDTO.getAddress());
                ud.setRole(userDTO.getRole());
                ud.setWarehouse(warehouseDTO.get());
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "User added successfully", ud);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Order detail added unsuccessfully", null);
        }
    }

    public ResponseObject<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> list = new ArrayList<>();
            userRepository.findAll().forEach(x -> {
                UserDTO userDTO = new UserDTO();
                Optional<WarehouseDTO> warehouseDTO = warehouseRepository.getWarehouseById(x.getWarehouse().getId());
                if (warehouseDTO.isEmpty()) {
                    throw new RuntimeException("Failed to fetch warehouses because of empty");
                }
                userDTO.setWarehouse(warehouseDTO.get());
                list.add(userDTO);
            });
            return new ResponseObject<>(HttpStatus.OK.value(), "Warehouses fetched successfully", list);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch warehouses", null);
        }
    }

//    public ResponseObject<OrderDetail> getOrderDetailById(int id) {
//        try {
//            Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
//            return orderDetail.map(detail -> new ResponseObject<>(HttpStatus.OK.value(), "Get order detail by id successfully", detail)).orElseGet(() -> new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Order detail not found", null));
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to fetch order detail", null);
//        }
//    }
//
//    public ResponseObject<OrderDetail> updateOrderDetail(int id, OrderDetailDTO orderDetailDTO) {
//        try {
//            Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
//            if (orderDetail.isPresent()) {
//                Optional<Order> order = orderRepository.getOrderById(orderDetailDTO.getOrderId());
//                if (order.isEmpty()) {
//                    throw new CustomException(ErrorCode.ORDER_INVALID);
//                }
//                Optional<Product> product = productRepository.getProductById(orderDetailDTO.getProductId());
//                if (product.isEmpty()) {
//                    throw new CustomException(ErrorCode.PRODUCT_ID_BLANK);
//                }
//                OrderDetail orderDetail1 = orderDetail.get();
//                orderDetail1.setOrder(order.get());
//                orderDetail1.setProduct(product.get());
//                orderDetail1.setQuantity(orderDetailDTO.getQuantity());
//                orderDetailRepository.save(orderDetail1);
//                return new ResponseObject<>(HttpStatus.OK.value(), "Updated order detail successfully", orderDetail1);
//            } else {
//                return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Not found", null);
//            }
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update order detail", null);
//        }
//    }
//
//    public ResponseObject<OrderDetail> deleteOrderDetailById(int id) {
//        try {
//            Optional<OrderDetail> orderDetail = orderDetailRepository.getOrderDetailById(id);
//            if (orderDetail.isPresent()) {
//                orderDetailRepository.delete(orderDetail.get());
//                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted order detail successfully", orderDetail.get());
//            } else {
//                return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), "Not found", null);
//            }
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete order detail", null);
//        }
//    }
//
//    public ResponseObject<Object> deleteAllOrderDetails() {
//        try {
//            List<OrderDetail> list = new ArrayList<>(orderDetailRepository.findAll());
//            if (!list.isEmpty()) {
//                orderDetailRepository.deleteAll();
//                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all order details successfully", null);
//            } else {
//                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No order detail in db", null);
//            }
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete order details", null);
//        }
//    }
}
