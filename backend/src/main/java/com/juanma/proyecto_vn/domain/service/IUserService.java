package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;

public interface IUserService {
    List<UserResponseDto> getAll();

    Map<String, Object> login(LoginDto user);

    Map<String, Object> saveUser(User user);

    Map<String, Object> saveAdmin(User user);

    Map<String, Object> updateUser(User user, UUID userId);

    boolean emailExist(String email);
}
