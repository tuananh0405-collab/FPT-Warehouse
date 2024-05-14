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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }

            List<UserDTO> userDTOs = users.stream()
                    .map(user -> {
                        UserDTO userDTO = UserMapper.INSTANCE.userToUserDTO(user);
                        WarehouseDTO warehouseDTO = WarehouseMapper.INSTANCE.warehouseToWarehouseDTO(user.getWarehouse());
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


    public ResponseObject<UserDTO> getUserById(int id) {
        try {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            UserDTO userDTO = UserMapper.INSTANCE.userToUserDTO(user.get());
            WarehouseDTO warehouseDTO = WarehouseMapper.INSTANCE.warehouseToWarehouseDTO(user.get().getWarehouse());
            userDTO.setWarehouse(warehouseDTO);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get user by id successfully", userDTO);
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
            if (userDTO.getEmail() != null &&
                    !userDTO.getEmail().trim().isEmpty() &&
                    !userDTO.getEmail().equals(user.get().getEmail()) &&
                    userRepository.existsByEmail(userDTO.getEmail())) {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            if (userDTO.getPhone() != null &&
                    !userDTO.getPhone().trim().isEmpty() &&
                    !userDTO.getPhone().equals(user.get().getPhone()) &&
                    userRepository.existsByPhone(userDTO.getPhone())) {
                throw new CustomException(ErrorCode.PHONE_ALREADY_EXISTS);
            }
            user.get().setFullName(userDTO.getFullName());
            user.get().setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.get().setRole(Role.valueOf(userDTO.getRole()));
            user.get().setEmail(userDTO.getEmail());
            user.get().setPhone(userDTO.getPhone());
            user.get().setAddress(userDTO.getAddress());
            userRepository.save(user.get());
            UserDTO userDTO1 = UserMapper.INSTANCE.userToUserDTO(user.get());
            return new ResponseObject<>(HttpStatus.OK.value(), "Update user successfully", userDTO1);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update user", null);
        }
    }
}
