package com.wha.warehousemanagement.service;

import com.wha.warehousemanagement.dto.request.LoginRequest;
import com.wha.warehousemanagement.dto.request.RefreshTokenRequest;
import com.wha.warehousemanagement.dto.request.SignUpRequest;
import com.wha.warehousemanagement.dto.response.LoginResponse;
import com.wha.warehousemanagement.dto.response.RefreshTokenResponse;
import com.wha.warehousemanagement.dto.response.SignUpResponse;
import com.wha.warehousemanagement.model.User;
import com.wha.warehousemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService  {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        SignUpResponse response = new SignUpResponse();
        try {
            User user = new User();
            user.setFullName(signUpRequest.getFullName());
            user.setUsername(signUpRequest.getUsername());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            user.setEmail(signUpRequest.getEmail());
            user.setPhone(signUpRequest.getPhone());
            user.setAddress(signUpRequest.getAddress());
            user.setRole(signUpRequest.getRole());
            User userResult = userRepository.save(user);
            if (userResult !=null && userResult.getId()>0) {
                response.setUser(userResult);
                response.setMessage("User saved successfully");
                response.setStatusCode(200);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            var user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();
            System.out.println("User is: " + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24h");
            response.setMessage("Successfully signed in");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse response = new RefreshTokenResponse();
        String ourUsername = jwtUtils.extractUsername(refreshTokenRequest.getToken());
        User users = userRepository.findByUsername(ourUsername).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRequest.getToken());
            response.setExpirationTime("24h");
            response.setMessage("Successfully refreshed in");
        }
        response.setStatusCode(500);
        return response;
    }

}
