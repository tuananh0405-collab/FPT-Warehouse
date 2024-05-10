package com.wha.warehousemanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wha.warehousemanagement.model.Role;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpRequest {

    private String fullName;

    private String username;

    private String password;

    private String email;

    private String phone;

    private String address;

    private Role role;

}
