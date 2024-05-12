package com.wha.warehousemanagement.repositories;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    Optional<Category> getCategoryById(int id);

    boolean existsByName(String name);

    @Query("SELECT new com.wha.warehousemanagement.dtos.CategoryDTO(c.id, c.name, c.description) FROM Category c WHERE c.id = :id")
    Optional<CategoryDTO> getCategoryDTOById(int id);


    @Query("SELECT new com.wha.warehousemanagement.dtos.CategoryDTO(c.id, c.name, c.description) FROM Category c")
    Optional<CategoryDTO> getAllCategoryDTO();
}
