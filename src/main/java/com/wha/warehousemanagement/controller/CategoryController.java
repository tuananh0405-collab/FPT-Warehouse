package com.wha.warehousemanagement.controller;

import com.wha.warehousemanagement.dto.request.AddCategoryRequest;
import com.wha.warehousemanagement.model.ResponseObject;
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

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addCategory(@RequestBody AddCategoryRequest addCategoryRequest) {
        return ResponseEntity.ok(new ResponseObject("200", "Category added successfully", categoryService.addCategory(addCategoryRequest)));
    }

}
