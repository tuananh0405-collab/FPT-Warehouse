package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.UserRequest;
import com.wha.warehousemanagement.dtos.responses.UserResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.UserMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ResponseObject<?> getAllUsers() {
        try {
            List<UserResponse> responses = userRepository.findAll()
                    .stream().map(userMapper::toDto)
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all users successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get users details", null);
        }
    }

    public ResponseObject<?> getUserById(int id) {
        try {
            UserResponse response = userRepository.findById(id)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Get user by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed get user by id", null);
        }
    }

    public ResponseObject<?> updateUser(int id, UserRequest request) {
        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new CustomException(ErrorCode.USER_NOT_FOUND)
            );
            user.setFullName(request.getFullName());
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setRole(Role.valueOf(request.getRole()));
            userRepository.save(user);
            UserResponse response = userMapper.toDto(user);
            return new ResponseObject<>(HttpStatus.OK.value(), "Update user successfully", response);
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
