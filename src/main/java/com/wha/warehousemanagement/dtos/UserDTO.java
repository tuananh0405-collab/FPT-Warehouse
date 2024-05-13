package com.wha.warehousemanagement.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wha.warehousemanagement.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private int id;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private WarehouseDTO warehouse;

    public UserDTO(int id, String fullName, String username, String password, String email, String phone, String address, Role role, WarehouseDTO warehouse) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        if (warehouse != null) {
            this.warehouse = new WarehouseDTO(warehouse.getId(), warehouse.getName(), warehouse.getDescription(), warehouse.getAddress());
        }
    }

    public UserDTO(int id, String fullName, String username, String password, String email, String phone, String address, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }
}
