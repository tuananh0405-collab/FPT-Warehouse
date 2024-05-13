package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.ProviderDTO;
import com.wha.warehousemanagement.dtos.UserDTO;
import com.wha.warehousemanagement.models.Provider;
import com.wha.warehousemanagement.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsById(int id);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
