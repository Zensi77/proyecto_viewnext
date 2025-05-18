package com.juanma.proyecto_vn.interfaces.rest.mapper;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    /**
     * Convierte un objeto CategoryRequestDto a un objeto de dominio Category
     * 
     * @param dto El DTO de la petición
     * @return Objeto de dominio Category
     */
    public Category toDomain(CategoryResponseDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    /**
     * Convierte un objeto Category de dominio a un DTO de respuesta
     * CategoryResponseDto
     * 
     * @param category Objeto de dominio Category
     * @return DTO de respuesta
     */
    public CategoryResponseDto toDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    /**
     * Convierte una lista de objetos Category a una lista de DTOs de respuesta
     * 
     * @param categories Lista de objetos de dominio
     * @return Lista de DTOs de respuesta
     */
    public List<CategoryResponseDto> toDtoList(List<Category> categories) {
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}