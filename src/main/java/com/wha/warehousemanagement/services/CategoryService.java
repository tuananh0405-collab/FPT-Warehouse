package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.dtos.requests.CategoryRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CategoryMapper;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ResponseObject<CategoryResponse> addCategory(CategoryRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_NAME_BLANK);
        } else if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        try {
            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            categoryRepository.save(category);
            Optional<CategoryResponse> response = getLastCategoryResponse();
            if (response.isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_ADD_FAILED);
            }
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Category added successfully",
                    response.get());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CATEGORY_ADD_FAILED);
        }
    }

    private Optional<CategoryResponse> getLastCategoryResponse() {
        List<CategoryResponse> responses = categoryRepository.getAllCategoryResponses();
        if (!responses.isEmpty()) {
            return Optional.of(responses.get(0));
        } else {
            return Optional.empty();
        }
    }

    public ResponseObject<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> list = new ArrayList<>();
        categoryRepository.findAll().forEach(category -> {
            CategoryResponse response = CategoryMapper.INSTANCE.categoryToCategoryResponse(category);
            list.add(response);
        });
        return new ResponseObject<>(HttpStatus.OK.value(), "Get all categories successfully", list);
    }

    public ResponseObject<CategoryResponse> getCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        CategoryResponse response = CategoryMapper.INSTANCE.categoryToCategoryResponse(category);
        return new ResponseObject<>(HttpStatus.OK.value(), "Get category by id successfully", response);
    }

    public ResponseObject<CategoryResponse> updateCategory(int id, CategoryRequest request) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            if (request.getName() != null &&
                    !request.getName().trim().isEmpty() &&
                    !request.getName().equals(category.getName()) &&
                    categoryRepository.existsByName(request.getName())) {
                throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
            }
            if (request.getName() != null) {
                category.setName(request.getName());
            }
            if (request.getDescription() != null) {
                category.setDescription(request.getDescription());
            }
            categoryRepository.save(category);
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Updated category successfully",
                    new CategoryResponse(category.getId(), category.getName(), category.getDescription()));
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
        try {
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
