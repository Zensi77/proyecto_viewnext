package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CategoryEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProviderEntity;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre entidades JPA y modelos de dominio
 */
@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final ProviderMapper providerMapper;

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        Category category = null;
        if (entity.getCategory() != null) {
            category = categoryMapper.toDomain(entity.getCategory());
        }

        Provider provider = null;
        if (entity.getProvider() != null) {
            provider = providerMapper.toDomain(entity.getProvider());
        }

        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .image(entity.getImage())
                .stock(entity.getStock())
                .description(entity.getDescription())
                .category(category)
                .provider(provider)
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }

        CategoryEntity categoryEntity = null;
        if (domain.getCategory() != null) {
            categoryEntity = categoryMapper.toEntity(domain.getCategory());
        }

        ProviderEntity providerEntity = null;
        if (domain.getProvider() != null) {
            providerEntity = providerMapper.toEntity(domain.getProvider());
        }

        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .price(domain.getPrice())
                .image(domain.getImage())
                .stock(domain.getStock())
                .description(domain.getDescription())
                .category(categoryEntity)
                .provider(providerEntity)
                .build();
    }
}