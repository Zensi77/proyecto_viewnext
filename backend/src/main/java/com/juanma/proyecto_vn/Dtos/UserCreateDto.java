package com.juanma.proyecto_vn.Dtos;

public class CreateUserDto {
    @isEmail
    @isNotEmpty
    @isNotBlank
    private String email;
}
