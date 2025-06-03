package com.juanma.proyecto_vn.interfaces.rest.dtos.product;

import java.util.UUID;

import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetProductDto {
    private UUID id;
    private String name;
    private double price;
    private String image;
    private String description;
    private int stock;
    private ProviderDto provider;
    private CategoryResponseDto category;
    private boolean isLiked;
}
