package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.ProviderDTO;
import com.wha.warehousemanagement.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByName(String name);

    Optional<Provider> getCategoryById(int id);

    boolean existsByName(String name);

    @Query("SELECT new com.wha.warehousemanagement.dtos.ProviderDTO(c.id, c.name, c.email, c.phone, c.address) FROM Provider c WHERE c.id = :id")
    Optional<ProviderDTO> getProviderDTOById(int id);

    @Query("SELECT new com.wha.warehousemanagement.dtos.ProviderDTO(c.id, c.name, c.email, c.phone, c.address) FROM Provider c")
    Optional<ProviderDTO> getAllProviderDTO();
}
