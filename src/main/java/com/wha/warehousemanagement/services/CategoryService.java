package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ResponseObject addCategory(CategoryDTO CategoryDTO) {
        if (CategoryDTO.getName() == null) {
            return new ResponseObject("400", "Name is blank", null);
        } else if (categoryRepository.findByName(CategoryDTO.getName()).isPresent()) {
            return new ResponseObject("400", "Category with name " + CategoryDTO.getName() + " already exists", null);
        }
        Category category = new Category();
        category.setName(CategoryDTO.getName());
        category.setDescription(CategoryDTO.getDescription());
        categoryRepository.save(category);
        return new ResponseObject("200", "Category added successfully", CategoryDTO);
    }

    public ResponseObject getAllCategories() {
        List<Category> list = new ArrayList<>(categoryRepository.findAll());
        return new ResponseObject("200", "Get all categories successfully", list);
    }

    public ResponseObject getCategoryById(int id) {
        Optional<CategoryDTO> category = categoryRepository.getCategoryDTOById(id);
        return category.map(
                        value -> new ResponseObject("200", "Get category successfully", value))
                .orElseGet(() -> new ResponseObject("500", "Not found", null));
    }

    public ResponseObject updateCategory(int id, CategoryDTO categoryDTO) {
        Optional<Category> category = categoryRepository.getCategoryById(id);
        if (category.isPresent()) {
            Category category1 = category.get();
            category1.setName(categoryDTO.getName());
            category1.setDescription(categoryDTO.getDescription());
            return new ResponseObject("200", "Updated category successfully", categoryRepository.save(category1));
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteCategoryById(int id) {
        Optional<Category> category = categoryRepository.getCategoryById(id);
        if (category.isPresent()) {
            categoryRepository.delete(category.get());
            return new ResponseObject("200", "Deleted category successfully", category.get());
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteAllCategories() {
        List<Category> list = new ArrayList<>(categoryRepository.findAll());
        if (!list.isEmpty()) {
            categoryRepository.deleteAll();
            return new ResponseObject("200", "Deleted all categories successfully", null);
        } else {
            return new ResponseObject("500", "No category in db", null);
        }

    }

}
