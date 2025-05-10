package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import java.util.List;

import com.juanma.proyecto_vn.shared.Utils.enums.paymentMethodEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    @NotEmpty(message = "El campo productOrder no puede estar vacío")
    private List<ProductOrderDto> productOrder;

    @NotNull(message = "El campo paymentMethod no puede ser nulo")
    private paymentMethodEnum paymentMethod;
}
