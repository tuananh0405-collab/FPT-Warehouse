package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ResponseObject<CategoryDTO> addCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_NAME_BLANK);
        } else if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        try {
            Category category = new Category();
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            categoryRepository.save(category);
            return new ResponseObject<>(HttpStatus.OK.value(), "Category added successfully", categoryDTO);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CATEGORY_ADD_FAILED);
        }
    }

    public ResponseObject<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> list = new ArrayList<>();
        categoryRepository.findAll().forEach(category -> {
            CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName(), category.getDescription());
            list.add(categoryDTO);
        });
        return new ResponseObject<>(HttpStatus.OK.value(), "Get all categories successfully", list);
    }

    public ResponseObject<CategoryDTO> getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName(), category.getDescription());
        return new ResponseObject<>(HttpStatus.OK.value(), "Get category by id successfully", categoryDTO);
    }

    public ResponseObject<CategoryDTO> updateCategory(int id, CategoryDTO categoryDTO) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            if (categoryDTO.getName() != null &&
                    !categoryDTO.getName().trim().isEmpty() &&
                    !categoryDTO.getName().equals(category.getName()) &&
                    categoryRepository.existsByName(categoryDTO.getName())) {
                throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
            }
            if (categoryDTO.getName() != null && !categoryDTO.getName().trim().isEmpty()) {
                category.setName(categoryDTO.getName());
            }
            if (categoryDTO.getDescription() != null) {
                category.setDescription(categoryDTO.getDescription());
            }
            categoryRepository.save(category);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated category successfully", categoryDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update category", null);
        }
    }

    public ResponseObject<Object> deleteCategoryById(int id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            categoryRepository.delete(category);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted category successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete category", null);
        }
    }

    public ResponseObject<Object> deleteAllCategories() {
        try{
        List<Category> list = categoryRepository.findAll();
        if (!list.isEmpty()) {
            categoryRepository.deleteAll();
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all categories successfully", null);
        } else {
            return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No categories to delete", null);
        }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete categories", null);
        }
    }

}
