package com.juanma.proyecto_vn.Dtos.Product;

import java.util.UUID;

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
    private String provider;
    private String category;
}
