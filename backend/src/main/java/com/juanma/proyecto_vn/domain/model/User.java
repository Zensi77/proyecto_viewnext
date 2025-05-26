package com.juanma.proyecto_vn.domain.model;

import java.util.*;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;

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
    private String username;
    private Set<Role> roles = new HashSet<>();
    private List<Product> wishlists = new ArrayList<>();
    private boolean enabled;
    private boolean accountNonLocked;
}