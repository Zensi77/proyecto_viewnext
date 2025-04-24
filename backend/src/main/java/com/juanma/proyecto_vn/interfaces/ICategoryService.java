package com.juanma.proyecto_vn.interfaces;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.Dtos.Category.CategoryDto;

public interface ICategoryService {
    List<CategoryDto> getAllCategories();

    CategoryDto getCategory(UUID id);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(UUID id, CategoryDto categoryDto);

    void deleteCategory(UUID id);

}
