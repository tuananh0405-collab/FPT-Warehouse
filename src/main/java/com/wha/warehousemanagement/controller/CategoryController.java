package com.wha.warehousemanagement.controller;

import com.wha.warehousemanagement.dto.request.AddCategoryRequest;
import com.wha.warehousemanagement.dto.response.AddCategoryResponse;
import com.wha.warehousemanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<AddCategoryResponse> addCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
        return ResponseEntity.ok(categoryService.addCategory(addCategoryRequest));
    }

}
