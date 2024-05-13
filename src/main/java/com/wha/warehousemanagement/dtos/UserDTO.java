package com.wha.warehousemanagement.dtos;


import com.wha.warehousemanagement.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private int id;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String role;
    WarehouseDTO warehouse;
}
