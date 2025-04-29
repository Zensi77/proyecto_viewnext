package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProductOrderDto {
    @NotNull(message = "El campo productId no puede ser nulo")
    private UUID productId;

    @NotNull(message = "El campo quantity no puede ser nulo")
    @Positive(message = "El campo quantity debe ser positivo")
    private int quantity;
}
