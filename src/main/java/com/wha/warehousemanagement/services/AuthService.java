package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.TokenDTO;
import com.wha.warehousemanagement.dtos.UserLoginDTO;
import com.wha.warehousemanagement.dtos.UserSignUpDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.User;
import com.wha.warehousemanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {

    private UserRepository userRepository;

    private JWTUtils jwtUtils;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, JWTUtils jwtUtils, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseObject signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.existsByUsername(userSignUpDTO.getUsername())) return new ResponseObject("500", "Username existed", null);
        if (userSignUpDTO.getPassword().length()<8) return new ResponseObject("500", "Password must be more than 8 characters", null);
        User user = new User();
        user.setFullName(userSignUpDTO.getFullName());
        user.setUsername(userSignUpDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userSignUpDTO.getPassword()));
        user.setEmail(userSignUpDTO.getEmail());
        user.setPhone(userSignUpDTO.getPhone());
        user.setAddress(userSignUpDTO.getAddress());
        user.setRole(userSignUpDTO.getRole());
        User userResult = userRepository.save(user);
        if (userResult != null && userResult.getId() > 0) {
            return new ResponseObject("200", "User signed up successfully", userResult);
        } else {
            return new ResponseObject("500", "User signed up unsuccessfully", null);
        }
    }


    public ResponseObject login(UserLoginDTO userLoginDTO) {
        TokenDTO tokenData = new TokenDTO();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
            var user = userRepository.findByUsername(userLoginDTO.getUsername()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            tokenData.setUsername(userLoginDTO.getUsername());
            tokenData.setToken(jwt);
            tokenData.setRefreshToken(refreshToken);
            tokenData.setExpirationTime("24h");
        } catch (Exception e) {
            return new ResponseObject("500", "Invalid username or passwords", null);
        }
        return new ResponseObject("200", "Login successfully", tokenData);
    }

    public ResponseObject refreshToken(String refreshTokenRequest) {
        TokenDTO tokenResponse = new TokenDTO();
        String ourUsername = jwtUtils.extractUsername(refreshTokenRequest);
        User users = userRepository.findByUsername(ourUsername).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenRequest, users)) {
            var jwt = jwtUtils.generateToken(users);
            tokenResponse.setToken(jwt);
            tokenResponse.setRefreshToken(refreshTokenRequest);
            tokenResponse.setExpirationTime("24h");
        } else {
            return new ResponseObject("500", "Invalid token", null);
        }
        return new ResponseObject("200", "Successfully refreshed in", null);
    }

}
