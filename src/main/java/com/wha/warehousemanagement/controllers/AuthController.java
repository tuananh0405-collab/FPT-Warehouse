package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.TokenDTO;
import com.wha.warehousemanagement.dtos.UserLoginDTO;
import com.wha.warehousemanagement.dtos.UserSignUpDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.User;
import com.wha.warehousemanagement.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseObject<Object>> signUp(@RequestBody UserSignUpDTO userSignUpDTO) {
        return ResponseEntity.ok(authService.signUp(userSignUpDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<TokenDTO>> login(@RequestBody UserLoginDTO userLoginDTO) {
        return ResponseEntity.ok(authService.login(userLoginDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseObject<Object>> refreshToken(@RequestBody String refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
}
