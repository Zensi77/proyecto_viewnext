package com.juanma.proyecto_vn.interfaces.rest.dtos.product;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull(message = "El stock no puede estar vacío")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Max(value = 1000, message = "El stock no puede ser mayor a 1000")
    private int stock;

    @NotBlank(message = "La imagen no puede estar vacía")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "La imagen debe ser una URL válida")
    private String image;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String description;

    @NotNull(message = "El proveedor no puede estar vacío")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "El proveedor debe ser un UUID válido")
    @Size(max = 36, message = "El proveedor no puede tener más de 36 caracteres")
    @NotBlank(message = "El proveedor no puede estar vacío")
    private String provider;

    @NotNull(message = "La categoría no puede estar vacía")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "La categoría debe ser un UUID válido")
    @Size(max = 36, message = "La categoría no puede tener más de 36 caracteres")
    @NotBlank(message = "La categoría no puede estar vacía")
    private String category;
}
