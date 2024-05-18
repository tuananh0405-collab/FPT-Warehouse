package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByName(String name);
    boolean existsByName(String name);
}
