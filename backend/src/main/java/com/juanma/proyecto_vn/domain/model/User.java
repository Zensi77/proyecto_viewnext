package com.juanma.proyecto_vn.domain.model;

import java.util.UUID;

import com.juanma.proyecto_vn.shared.Utils.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio para Usuario, independiente de la infraestructura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private RoleEnum role;
}