package com.juanma.proyecto_vn.interfaces.rest.dtos.auth;

import com.juanma.proyecto_vn.interfaces.rest.validation.UniqueEmail;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Getter
@Setter
@Builder
public class UserCreateDto {
    @NotNull(message = "El nombre no puede ser nulo")
    @Email(message = "Email no válido")
    @UniqueEmail(message = "El email ya está en uso")
    private String email;

    @NotNull(message = "La contraseña no puede ser nula")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "La contraseña debe contener al menos una letra mayúscula, una letra minúscula y un número")
    private String password;

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String name;

    @Pattern(regexp = "USER|ADMIN", message = "El rol debe ser 'USER' o 'ADMIN'")
    private String role;
}
