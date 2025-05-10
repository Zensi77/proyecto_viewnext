package com.juanma.proyecto_vn.interfaces.rest.mapper;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import org.springframework.stereotype.Component;

/**
 * Mapeador para convertir entre modelo de dominio y DTOs para la API REST
 */
@Component
public class ProductDtoMapper {
    /**
     * Convierte una entidad de dominio a un DTO para la API
     */
    public GetProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        GetProductDto productDto = GetProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .description(product.getDescription())
                .category(product.getCategory() != null ? CategoryResponseDto.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build() : null)
                .provider(product.getProvider() != null ? ProviderDto.builder()
                        .id(product.getProvider().getId())
                        .name(product.getProvider().getName())
                        .address(product.getProvider().getAddress())
                        .build() : null)
                .stock(product.getStock())
                .build();

        // Manejo seguro de categorías
        Category category = product.getCategory();
        if (category != null) {
            productDto = GetProductDto.builder()
                    .id(productDto.getId())
                    .name(productDto.getName())
                    .price(productDto.getPrice())
                    .image(productDto.getImage())
                    .description(productDto.getDescription())
                    .stock(productDto.getStock())
                    .category(CategoryResponseDto.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .build())
                    .build();
        }

        // Manejo seguro de proveedores
        Provider provider = product.getProvider();
        if (provider != null) {
            productDto = GetProductDto.builder()
                    .id(productDto.getId())
                    .name(productDto.getName())
                    .price(productDto.getPrice())
                    .image(productDto.getImage())
                    .description(productDto.getDescription())
                    .stock(productDto.getStock())
                    .category(productDto.getCategory())
                    .provider(ProviderDto.builder()
                            .id(provider.getId())
                            .name(provider.getName())
                            .address(provider.getAddress())
                            .build())
                    .build();
        }

        return productDto;
    }

    public Product toDomain(GetProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .image(productDto.getImage())
                .description(productDto.getDescription())
                .stock(productDto.getStock())
                .category(null) // Asignar la categoría según sea necesario
                .provider(null) // Asignar el proveedor según sea necesario
                .build();
    }
}