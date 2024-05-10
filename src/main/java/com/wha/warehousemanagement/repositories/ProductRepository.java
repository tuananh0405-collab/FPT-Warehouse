package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
