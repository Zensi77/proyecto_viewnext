package com.juanma.proyecto_vn.Dtos.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDto {

    private String email;

    private String name;

    private String role;
}
