package com.hieuphung.SpringSecurityJWT.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hieuphung.SpringSecurityJWT.model.Role;
import com.hieuphung.SpringSecurityJWT.model.User;
import com.hieuphung.SpringSecurityJWT.model.Product;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {

    private int statusCode;

    private String error;

    private String message;

    private String token;

    private String refreshToken;

    private String expirationTime;

    private String fullName;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String address;

    private Role role;

    private List<Product> products;

    private User user;

}
