package com.juanma.proyecto_vn.interfaces.rest.dtos.cart;

import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetProductCartDto {
    private GetProductDto product;

    private int quantity;
}
