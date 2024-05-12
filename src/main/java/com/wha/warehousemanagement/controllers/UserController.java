package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.UserDTO;
import com.wha.warehousemanagement.dtos.WarehouseDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.User;
import com.wha.warehousemanagement.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

// Because we already have a method to sign up a user, we don't need this method
//    @PostMapping
//    public ResponseEntity<ResponseObject<Object>> addUser(@RequestBody UserDTO userDTO) {
//        return ResponseEntity.ok(userService.addUser(userDTO));
//    }

    @GetMapping("/all")
    public ResponseEntity<ResponseObject<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<UserDTO>> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUsersById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

}
