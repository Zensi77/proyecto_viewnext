package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CategoryEntity;

import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades JPA y modelos de dominio para
 * categorías
 */
@Component("categoryPersistenceMapper")
public class CategoryMapper {

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public Category toDomain(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public CategoryEntity toEntity(Category domain) {
        if (domain == null) {
            return null;
        }

        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }
}