package com.juanma.proyecto_vn.domain.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para Categoría, independiente de la infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private UUID id;
    private String name;
}