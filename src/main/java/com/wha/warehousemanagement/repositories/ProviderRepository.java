package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.dtos.responses.ProviderResponse;
import com.wha.warehousemanagement.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Integer> {
    Optional<Provider> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT new com.wha.warehousemanagement.dtos.responses.ProviderResponse(c.id, c.name, c.email, c.phone, c.address) FROM Provider c ORDER BY c.id desc")
    List<ProviderResponse> getAllProviderResponses();
}
