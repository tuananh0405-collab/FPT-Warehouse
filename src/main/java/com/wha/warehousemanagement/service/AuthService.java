package com.wha.warehousemanagement.service;

import com.wha.warehousemanagement.dto.ReqRes;
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

    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            User user = new User();
            user.setFullName(registrationRequest.getFullName());
            user.setUsername(registrationRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setEmail(registrationRequest.getEmail());
            user.setPhone(registrationRequest.getPhone());
            user.setAddress(registrationRequest.getAddress());
            user.setRole(registrationRequest.getRole());
            User userResult = userRepository.save(user);
            if (userResult !=null && userResult.getId()>0) {
                reqRes.setUser(userResult);
                reqRes.setMessage("User saved successfully");
                reqRes.setStatusCode(200);
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes reqRes = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
            var user = userRepository.findByUsername(signinRequest.getUsername()).orElseThrow();
            System.out.println("User is: " + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshToken);
            reqRes.setExpirationTime("24h");
            reqRes.setMessage("Successfully signed in");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setError(e.getMessage());
        }
        return reqRes;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRegister) {
        ReqRes reqRes = new ReqRes();
        String ourUsername = jwtUtils.extractUsername(refreshTokenRegister.getToken());
        User users = userRepository.findByUsername(ourUsername).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenRegister.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            reqRes.setStatusCode(200);
            reqRes.setToken(jwt);
            reqRes.setRefreshToken(refreshTokenRegister.getToken());
            reqRes.setExpirationTime("24h");
            reqRes.setMessage("Successfully refreshed in");
        }
        reqRes.setStatusCode(500);
        return reqRes;
    }

}
