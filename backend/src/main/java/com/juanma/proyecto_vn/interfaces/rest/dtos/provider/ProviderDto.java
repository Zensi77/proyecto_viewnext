package com.juanma.proyecto_vn.interfaces.rest.dtos.provider;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderDto {
    private UUID id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @NotEmpty(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "La direccion no puede estar vacía")
    @NotEmpty(message = "La direccion no puede estar vacía")
    private String address;
}
