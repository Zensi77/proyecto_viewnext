package com.juanma.proyecto_vn.Dtos.Product;

import java.util.UUID;

import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;
import com.juanma.proyecto_vn.Dtos.Category.CategoryDto;

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
    private CategoryDto category;
}
