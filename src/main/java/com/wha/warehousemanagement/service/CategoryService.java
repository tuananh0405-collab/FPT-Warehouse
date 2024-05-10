package com.wha.warehousemanagement.service;

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

    @Autowired
    private CategoryRepository categoryRepository;

    public AddCategoryResponse addCategory(AddCategoryRequest addCategoryRequest) {
        AddCategoryResponse response = new AddCategoryResponse();
        try {
            Category category = new Category();
            category.setName(addCategoryRequest.getName());
            category.setDescription(addCategoryRequest.getDescription());
            Category categoryResult = categoryRepository.save(category);
            if (categoryResult !=null && categoryResult.getId()>0) {
                response.setCategory(category);
                response.setMessage("Category saved successfully");
                response.setStatusCode(200);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

}
