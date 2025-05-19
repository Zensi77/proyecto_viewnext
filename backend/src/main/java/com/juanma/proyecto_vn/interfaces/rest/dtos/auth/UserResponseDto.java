package com.juanma.proyecto_vn.interfaces.rest.dtos.auth;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto {
    private UUID id;

    private String email;

    private String name;

    private Set<Role> roles;

    private boolean enabled;

    private boolean accountNonLocked;
}
