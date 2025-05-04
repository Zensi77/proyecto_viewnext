package com.juanma.proyecto_vn.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para Producto, independiente de la infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private String name;
    private Double price;
    private String image;
    private int stock;
    private String description;
    private Category category;
    private Provider provider;
}
