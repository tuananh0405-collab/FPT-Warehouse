package com.hieuphung.SpringSecurityJWT.repository;

import com.hieuphung.SpringSecurityJWT.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
