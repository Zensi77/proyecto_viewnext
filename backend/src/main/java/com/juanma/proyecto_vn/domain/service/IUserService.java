package com.juanma.proyecto_vn.domain.service;

import java.util.Map;

import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserCreateDto;

public interface IUserService {
    Map<String, Object> login(LoginDto user);

    Map<String, Object> saveUser(UserCreateDto user);

    Map<String, Object> saveAdmin(UserCreateDto user);

    boolean emailExist(String email);
}
