package com.juanma.proyecto_vn.interfaces.rest.dtos.order;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductOrderDto {
    @NotNull(message = "El campo productId no puede ser nulo")
    private UUID productId;

    @NotNull(message = "El campo quantity no puede ser nulo")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 100, message = "La cantidad máxima es 100")
    private int quantity;
}
