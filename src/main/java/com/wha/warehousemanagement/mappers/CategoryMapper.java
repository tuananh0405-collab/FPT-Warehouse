package com.wha.warehousemanagement.mappers;

import com.wha.warehousemanagement.dtos.CategoryDTO;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);
    CategoryResponse categoryToCategoryResponse(Category category);
    Category categoryDTOToCategory(CategoryDTO categoryDTO);
}
