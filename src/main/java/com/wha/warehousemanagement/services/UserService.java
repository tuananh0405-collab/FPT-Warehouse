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

    public ResponseObject<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> list = new ArrayList<>(userRepository.getAllUsers());
            if (list.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            list.forEach(userDTO -> {
                Optional<WarehouseDTO> warehouseDTO = warehouseRepository.getWarehouseByUserId(userDTO.getId());
                warehouseDTO.ifPresent(userDTO::setWarehouse);
            });
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all users successfully", list);
        } catch (CustomException e) {
            return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get users details", null);
        }
    }

    public ResponseObject<UserDTO> getUsersById(int id) {
        try {
            Optional<UserDTO> userDTO = userRepository.getUserDTOById(id);
            if (userDTO.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            Optional<WarehouseDTO> warehouseDTO = warehouseRepository.getWarehouseByUserId(userDTO.get().getId());
            warehouseDTO.ifPresent(userDTO.get()::setWarehouse);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get user by id successfully", userDTO.get());
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get user by id", null);
        }
    }

    public ResponseObject<Object> updateUser(int id, UserDTO userDTO) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            if (userDTO.getUsername() != null &&
                    !userDTO.getUsername().trim().isEmpty() &&
                    !userDTO.getUsername().equals(user.get().getUsername()) &&
                    userRepository.existsByUsername(userDTO.getUsername())) {
                throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
            }
            user.get().setUsername(userDTO.getUsername());
            user.get().setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.get().setFullName(userDTO.getFullName());
            user.get().setEmail(userDTO.getEmail());
            user.get().setAddress(userDTO.getAddress());
            user.get().setRole(userDTO.getRole());
            UserDTO updatedUserDTO = new UserDTO(user.get().getId(), user.get().getFullName(),
                    user.get().getUsername(), user.get().getPassword(), user.get().getEmail(), user.get().getPhone(),
                    user.get().getAddress(), user.get().getRole());
            userRepository.save(user.get());
            return new ResponseObject<>(HttpStatus.OK.value(), "Update user successfully", updatedUserDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update user", null);
        }
    }

    public ResponseObject<Object> updateWarehouseForUser(int userId, int warehouseId) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
            if (warehouse.isEmpty()) {
                throw new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND);
            }
            user.get().setWarehouse(warehouse.get());
            userRepository.save(user.get());
            return new ResponseObject<>(HttpStatus.OK.value(), "Update warehouse for user successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update warehouse for user", null);
        }
    }

}
