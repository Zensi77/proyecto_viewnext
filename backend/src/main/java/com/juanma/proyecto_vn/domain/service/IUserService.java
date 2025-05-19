package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.Map;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserCreateDto;

public interface IUserService {
    List<User> getAll();

    Map<String, Object> login(LoginDto user);

    Map<String, Object> saveUser(UserCreateDto user);

    Map<String, Object> saveAdmin(UserCreateDto user);

    boolean emailExist(String email);
}
