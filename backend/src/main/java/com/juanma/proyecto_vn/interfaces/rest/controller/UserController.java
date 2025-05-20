package com.juanma.proyecto_vn.interfaces.rest.controller;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.service.IUserService;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserCreateDto;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserDtoMapper userDtoMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        User userDomain = userDtoMapper.toDomain(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(userDomain));
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')") // Solo el admin puede crear usuarios admin
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody UserCreateDto user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        Map<String, Object> userCreated = userService.saveAdmin(userDtoMapper.toDomain(user));

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

            Map<String, Object> response = userService.login(user);
            return ResponseEntity.ok(response);

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDto user, @PathVariable UUID userId, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        User userDomain = userDtoMapper.toDomain(user);
        Map<String, Object> userUpdated = userService.updateUser(userDomain, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(userUpdated);
    }

    @GetMapping("/email-exist")
    public ResponseEntity<Boolean> emailExist(@RequestParam String email) {
        return ResponseEntity.ok(userService.emailExist(email));
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put("message", "Error en campo " + err.getField() + ": " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}