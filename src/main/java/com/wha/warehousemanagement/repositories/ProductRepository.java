package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.responses.ProductResponse;
import com.wha.warehousemanagement.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
    Optional<Product> getProductById(int id);
}
