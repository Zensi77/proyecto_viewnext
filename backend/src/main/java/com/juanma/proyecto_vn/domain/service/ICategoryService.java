package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryDto;

public interface ICategoryService {
    List<CategoryDto> getAllCategories();

    CategoryDto getCategory(UUID id);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(UUID id, CategoryDto categoryDto);

    void deleteCategory(UUID id);

}
