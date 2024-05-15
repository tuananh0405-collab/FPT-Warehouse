package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.UserDTO;
import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.UserMapper;
import com.wha.warehousemanagement.mappers.WarehouseMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.UserRepository;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WarehouseRepository warehouseRepository;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WarehouseMapper warehouseMapper;

    public ResponseObject<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> {
                        UserDTO userDTO = userMapper.toDto(user);
                        WarehouseDTO warehouseDTO = warehouseMapper.toDto(user.getWarehouse());
                        userDTO.setWarehouse(warehouseDTO);
                        return userDTO;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all users successfully", userDTOs);
        } catch (CustomException e) {
            return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get users details", null);
        }
    }

    public ResponseObject<?> getUserById(int id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            UserDTO userDTO = userMapper.toDto(user);
            WarehouseDTO warehouseDTO = warehouseMapper.toDto(user.getWarehouse());
            userDTO.setWarehouse(warehouseDTO);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get user by id successfully", userDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get user by id", null);
        }
    }

    public ResponseObject<?> updateUser(int id, UserDTO userReceived) {
        try {
            User updatedUser = userRepository.findById(id).map(user -> {
                user.setFullName(userReceived.getFullName());
                user.setUsername(userReceived.getUsername());
                user.setPassword(userReceived.getPassword());
                user.setEmail(userReceived.getEmail());
                user.setPhone(userReceived.getPhone());
                user.setAddress(userReceived.getAddress());
                user.setRole(Role.valueOf(userReceived.getRole()));
                return userRepository.save(user);
            }).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            if (userReceived.getWarehouse() != null) {
                Warehouse warehouse = warehouseRepository.findById(userReceived.getWarehouse().getId()).orElseThrow(
                        () -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)
                );
                updatedUser.setWarehouse(warehouse);
            }

            return new ResponseObject<>(HttpStatus.OK.value(), "Update user successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed update user", null);
        }
    }

    public ResponseObject<?> deleteUserById(int id) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            userRepository.delete(user);
            return new ResponseObject<>(HttpStatus.OK.value(), "Delete user successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed delete user", null);
        }
    }
}
