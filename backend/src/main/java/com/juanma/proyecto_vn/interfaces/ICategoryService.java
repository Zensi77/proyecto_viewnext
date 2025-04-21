package com.juanma.proyecto_vn.interfaces;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.Dtos.category.CreateCategoryDto;

public interface ICategoryService {
    List<CreateCategoryDto> getAllCategories();

    CreateCategoryDto getCategory(UUID id);

    CreateCategoryDto createCategory(CreateCategoryDto categoryDto);

    CreateCategoryDto updateCategory(UUID id, CreateCategoryDto categoryDto);

    void deleteCategory(UUID id);

}
