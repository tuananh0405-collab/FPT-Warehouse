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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WarehouseRepository warehouseRepository;

    private final UserRepository userRepository;

    public ResponseObject<List<UserDTO>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> {
                        UserDTO userDTO = UserMapper.INSTANCE.toDto(user);
                        WarehouseDTO warehouseDTO = WarehouseMapper.INSTANCE.toDto(user.getWarehouse());
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
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            UserDTO userDTO = UserMapper.INSTANCE.toDto(user);
            WarehouseDTO warehouseDTO = WarehouseMapper.INSTANCE.toDto(user.getWarehouse());
            userDTO.setWarehouse(warehouseDTO);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get user by id successfully", userDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get user by id", null);
        }
    }

    public ResponseObject<UserDTO> updateUser(int id, UserDTO userReceived) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );

            user.setFullName(userReceived.getFullName());
            user.setUsername(userReceived.getUsername());
            user.setPassword(userReceived.getPassword());
            user.setEmail(userReceived.getEmail());
            user.setPhone(userReceived.getPhone());
            user.setAddress(userReceived.getAddress());
            user.setRole(Role.valueOf(userReceived.getRole()));

            //should be updated by warehouse id
            Optional<Warehouse> warehouse = warehouseRepository.findById(userReceived.getWarehouse().getId());
            warehouse.ifPresent(user::setWarehouse);
            user = userRepository.save(user);

            return new ResponseObject<>(HttpStatus.OK.value(), "Update user successfully", UserMapper.INSTANCE.toDto(user));
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed update user", null);
        }
    }

    public ResponseObject<Object> deleteUserById(int id) {
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
