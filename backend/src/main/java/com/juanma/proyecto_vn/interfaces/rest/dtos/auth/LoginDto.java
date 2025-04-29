package com.juanma.proyecto_vn.interfaces.rest.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
    @NotNull(message = "El email no puede ser nulo")
    @Email(message = "Email no válido")
    private String email;

    @NotNull(message = "La contraseña no puede ser nula")
    private String password;
}