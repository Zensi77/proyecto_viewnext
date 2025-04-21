package com.juanma.proyecto_vn.Dtos.Product;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateProductDto {
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Positive(message = "El precio debe ser un número positivo")
    private double price;

    @NotBlank(message = "La imagen no puede estar vacía")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "La imagen debe ser una URL válida")
    private String image;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String description;

    @NotNull(message = "El proveedor no puede estar vacío")
    private UUID provider;

    @NotNull(message = "La categoría no puede estar vacía")
    private UUID category;
}
