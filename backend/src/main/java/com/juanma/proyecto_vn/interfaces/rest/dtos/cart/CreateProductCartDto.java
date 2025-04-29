package com.juanma.proyecto_vn.interfaces.rest.dtos.cart;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCartDto {
    @NotNull(message = "El campo cart_id no puede ser nulo")
    private UUID cart_id;

    @NotNull(message = "El campo product_id no puede ser nulo")
    private UUID product_id;

    @NotNull(message = "El campo quantity no puede ser nulo")
    @Min(value = 1, message = "El campo quantity no puede ser menor que 1")
    @Max(value = 100, message = "El campo quantity no puede ser mayor que 100")
    private int quantity;
}
