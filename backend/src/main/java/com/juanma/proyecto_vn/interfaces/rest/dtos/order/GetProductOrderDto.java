package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetProductOrderDto {
    private GetProductDto product;
    private int quantity;
}
