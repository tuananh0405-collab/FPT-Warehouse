package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addCategory(@Valid @RequestBody CategoryDTO CategoryDTO) {
        return ResponseEntity.ok(categoryService.addCategory(CategoryDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getAllCategories(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCategory(@PathVariable("id") int id, @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.deleteCategoryById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject> deleteAllCategories() {
        return ResponseEntity.ok(categoryService.deleteAllCategories());
    }

}
