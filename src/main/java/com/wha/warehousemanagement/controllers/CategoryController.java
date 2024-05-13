package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject<CategoryDTO>> addCategory(@RequestBody CategoryDTO CategoryDTO) {
        return ResponseEntity.ok(categoryService.addCategory(CategoryDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<CategoryDTO>>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<CategoryDTO>> getCategoryById(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<CategoryDTO>> updateCategory(@PathVariable("id") int id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> deleteCategory(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.deleteCategoryById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject<Object>> deleteAllCategories() {
        return ResponseEntity.ok(categoryService.deleteAllCategories());
    }

}
