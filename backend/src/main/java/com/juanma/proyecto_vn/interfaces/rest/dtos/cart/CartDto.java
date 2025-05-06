package com.juanma.proyecto_vn.interfaces.rest.dtos.cart;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private String cart_id;
    private List<GetProductCartDto> products;
    private Double totalPrice;
}
