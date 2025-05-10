package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import com.juanma.proyecto_vn.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderDto {
    private Product product;
    private int quantity;
}
