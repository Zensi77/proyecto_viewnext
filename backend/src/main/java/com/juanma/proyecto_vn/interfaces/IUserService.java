package com.juanma.proyecto_vn.interfaces;

import java.util.Map;

import com.juanma.proyecto_vn.Dtos.Auth.LoginDto;
import com.juanma.proyecto_vn.Dtos.Auth.UserCreateDto;

public interface IUserService {
    Map<String, Object> login(LoginDto user);

    Map<String, Object> saveUser(UserCreateDto user);

    Map<String, Object> saveAdmin(UserCreateDto user);

    boolean emailExist(String email);
}
