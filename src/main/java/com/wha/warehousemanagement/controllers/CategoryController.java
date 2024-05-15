package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.dtos.requests.CategoryRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseObject<CategoryResponse>> addCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.addCategory(request));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<CategoryResponse>> getCategoryById(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<CategoryResponse>> updateCategory(@PathVariable("id") int id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
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
