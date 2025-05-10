package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.shared.Utils.enums.paymentMethodEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderDto {
    private UUID id;
    private List<ProductOrderDto> items;
    private Double totalPrice;
    private String status;
    private paymentMethodEnum paymentMethod;
    private LocalDateTime createdAt;
}
