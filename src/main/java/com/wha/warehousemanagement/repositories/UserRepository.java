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
    Optional<User> getUserById(int id);

    boolean existsById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.UserDTO(c.id, c.fullName, c.username, c.password, c.email, c.phone, c.address, c.role) FROM User c WHERE c.id = :id")
    Optional<UserDTO> getUserDTOById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.UserDTO(c.id, c.fullName, c.username, c.password, c.email, c.phone, c.address, c.role) FROM User c")
    List<UserDTO> getAllUsers();
}
