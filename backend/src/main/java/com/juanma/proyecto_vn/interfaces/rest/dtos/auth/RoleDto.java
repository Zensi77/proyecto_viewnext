package com.juanma.proyecto_vn.interfaces.rest.dtos.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    @NotNull(message = "El nombre no puede ser nulo")
    @Pattern(regexp = "ROLE_ADMIN|ROLE_USER", message = "El nombre del rol no es válido")
    private String authority;
}
