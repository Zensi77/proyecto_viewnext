package com.juanma.proyecto_vn.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Product product;
    private Cart cart;
    private int quantity;
}
