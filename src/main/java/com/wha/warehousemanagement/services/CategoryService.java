package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ResponseObject addCategory(CategoryDTO CategoryDTO) {
        if(CategoryDTO.getName() == null) {
            throw new IllegalArgumentException("Category name cannot be null");
        } else if (categoryRepository.findByName(CategoryDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with name " + CategoryDTO.getName() + " already exists");
        }
        Category category = new Category();
        category.setName(CategoryDTO.getName());
        category.setDescription(CategoryDTO.getDescription());
        categoryRepository.save(category);
        return new ResponseObject("200","Category added successfully", CategoryDTO);
    }

}
