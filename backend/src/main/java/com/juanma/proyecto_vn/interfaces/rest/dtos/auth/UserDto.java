package com.juanma.proyecto_vn.interfaces.rest.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@Getter
@Builder
public class UserDto {

    @NotNull(message = "El id no puede ser nulo")
    private UUID id;

    @NotNull(message = "El email no puede ser nulo")
    private String email;

    private String password;

    @NotNull(message = "El nombre de usuario no puede ser nulo")
    private String username;

    @NotNull(message = "El rol no puede ser nulo")
    @Size(min = 1, message = "El rol no puede estar vacío")
    @JsonProperty("roles") // Indica que el nombre del campo en el JSON es "roles"
    @JsonDeserialize(contentAs = RoleDto.class) // Indica que el contenido de la lista es de tipo Role
    private Set<@Valid RoleDto> roles;

    private Boolean enabled;

    private Boolean accountNonLocked ;
}
