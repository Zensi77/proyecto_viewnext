package com.juanma.proyecto_vn.interfaces.rest.dtos.auth;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    Set<RoleDto> roles;
    private boolean enabled = true;
    private boolean accountNonLocked = true;
}
