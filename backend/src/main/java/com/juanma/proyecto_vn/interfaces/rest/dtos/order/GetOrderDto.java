package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import java.time.LocalDateTime;
import java.util.List;

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
    private String id;
    private List<GetProductOrderDto> productOrder;
    private Double total_price;
    private String status;
    private paymentMethodEnum paymentMethod;
    private LocalDateTime createdAt;
}
