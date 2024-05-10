package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.UserLoginDTO;
import com.wha.warehousemanagement.dtos.UserSignUpDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseObject> signUp(@RequestBody UserSignUpDTO userSignUpDTO) {
        return ResponseEntity.ok(authService.signUp(userSignUpDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(authService.login(userLoginDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject> refreshToken(@RequestBody String refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
}
