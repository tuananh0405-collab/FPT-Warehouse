package com.wha.warehousemanagement.service;

import com.wha.warehousemanagement.dto.CategoryDTO;
import com.wha.warehousemanagement.dto.request.AddCategoryRequest;
import com.wha.warehousemanagement.dto.request.SignUpRequest;
import com.wha.warehousemanagement.dto.response.AddCategoryResponse;
import com.wha.warehousemanagement.dto.response.SignUpResponse;
import com.wha.warehousemanagement.model.Category;
import com.wha.warehousemanagement.model.User;
import com.wha.warehousemanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDTO addCategory(AddCategoryRequest addCategoryRequest) {
        CategoryDTO categoryDTO = new CategoryDTO();
        Category category = new Category();
        category.setName(addCategoryRequest.getName());
        category.setDescription(addCategoryRequest.getDescription());
        categoryRepository.save(category);
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        return categoryDTO;
    }

}
