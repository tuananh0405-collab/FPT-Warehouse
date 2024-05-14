package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.TokenDTO;
import com.wha.warehousemanagement.dtos.UserLoginDTO;
import com.wha.warehousemanagement.dtos.UserSignUpDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.models.User;
import com.wha.warehousemanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final JWTUtils jwtUtils;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public ResponseObject<Object> signUp(UserSignUpDTO userSignUpDTO) {
        try {
            if (userRepository.existsByUsername(userSignUpDTO.getUsername())) {
                throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
            }
            if (userSignUpDTO.getPassword().length() < 8) {
                throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
            }
            User user = new User(
                    userSignUpDTO.getFullName(),
                    userSignUpDTO.getUsername(),
                    passwordEncoder.encode(userSignUpDTO.getPassword()),
                    userSignUpDTO.getEmail(),
                    userSignUpDTO.getPhone(),
                    userSignUpDTO.getAddress(),
                    userSignUpDTO.getRole()
            );
            User userResult = userRepository.save(user);
            return new ResponseObject<>(HttpStatus.OK.value(), "User signed up successfully", userResult);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "User signed up UN-successfully", null);
        }
    }

    public ResponseObject<TokenDTO> login(UserLoginDTO userLoginDTO) {
        try {
            TokenDTO tokenData = new TokenDTO();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));
            User user = userRepository.findByUsername(userLoginDTO.getUsername()).orElseThrow();

            System.out.println(user.getUsername() + " " + user.getPassword());

            String  jwt = jwtUtils.generateToken(user);
            String  refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            tokenData.setUsername(userLoginDTO.getUsername());
            tokenData.setToken(jwt);
            tokenData.setRefreshToken(refreshToken);
            tokenData.setExpirationTime("24h");
            return new ResponseObject<>(HttpStatus.OK.value(), "Login successfully", tokenData);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Invalid username or password", null);
        }
    }

    public ResponseObject<Object> refreshToken(String refreshTokenRequest) {
        try {
            TokenDTO tokenResponse = new TokenDTO();
            String ourUsername = jwtUtils.extractUsername(refreshTokenRequest);
            User users = userRepository.findByUsername(ourUsername).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest, users)) {
                var jwt = jwtUtils.generateToken(users);
                tokenResponse.setToken(jwt);
                tokenResponse.setRefreshToken(refreshTokenRequest);
                tokenResponse.setExpirationTime("24h");
            } else {
                return new ResponseObject<>(400, "Invalid refresh token", null);
            }
            return new ResponseObject<>(200, "Successfully refreshed in", null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Invalid refresh token", null);
        }
    }

}
