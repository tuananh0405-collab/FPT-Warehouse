package com.wha.warehousemanagement.controller;

import com.wha.warehousemanagement.dto.ReqRes;
import com.wha.warehousemanagement.dto.request.LoginRequest;
import com.wha.warehousemanagement.dto.request.RefreshTokenRequest;
import com.wha.warehousemanagement.dto.request.SignUpRequest;
import com.wha.warehousemanagement.dto.response.LoginResponse;
import com.wha.warehousemanagement.dto.response.RefreshTokenResponse;
import com.wha.warehousemanagement.dto.response.SignUpResponse;
import com.wha.warehousemanagement.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }
}
