package com.wha.warehousemanagement.repository;

import com.wha.warehousemanagement.model.Category;
import com.wha.warehousemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
//    Optional<Category> addCategory(String username);
}
