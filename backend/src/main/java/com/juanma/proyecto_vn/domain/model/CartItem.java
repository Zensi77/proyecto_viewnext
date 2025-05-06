package com.juanma.proyecto_vn.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de dominio para los items del carrito de compras
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Product product;

    private Cart cart; // Evita la recursión infinita en toString() y equals/hashCode

    private int quantity;
}
