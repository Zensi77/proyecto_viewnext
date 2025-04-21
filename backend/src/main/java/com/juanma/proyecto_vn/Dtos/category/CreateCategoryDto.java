package com.juanma.proyecto_vn.Dtos.category;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCategoryDto {

    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Name is required")
    private String name;
}