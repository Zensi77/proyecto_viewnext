package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProductOrderDto {
    private GetProductDto product;

    private int quantity;
}
