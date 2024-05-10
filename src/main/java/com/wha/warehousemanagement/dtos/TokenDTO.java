package com.wha.warehousemanagement.dtos;

import lombok.Data;

@Data
public class TokenDTO {
    private String username;
    private String token;
    private String refreshToken;
    private String expirationTime;
}
