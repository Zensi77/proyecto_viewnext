package com.juanma.proyecto_vn.interfaces.rest.mapper;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryRequestDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryResponseDto;

@Component
public class CategoryDtoMapper {
    public CategoryResponseDto toDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toDomain(CategoryRequestDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        return Category.builder().name(categoryDto.getName())
                .build();
    }
}
