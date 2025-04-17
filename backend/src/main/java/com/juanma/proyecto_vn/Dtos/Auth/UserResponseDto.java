package com.juanma.proyecto_vn.Dtos.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto {
    @NotNull(message = "El nombre no puede ser nulo")
    @Email(message = "Email no válido")
    private String email;

    @Pattern(regexp = "USER|ADMIN", message = "El rol debe ser 'USER' o 'ADMIN'")
    private String role;
}
