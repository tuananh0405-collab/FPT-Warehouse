package com.wha.warehousemanagement.repository;

import com.wha.warehousemanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
