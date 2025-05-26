package com.juanma.proyecto_vn.interfaces.rest.mapper;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.RoleType;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.RoleDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserCreateDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {

    @Autowired
    private ProductDtoMapper productDtoMapper;

    public UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }
        Set<RoleDto> roles = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            roles = user.getRoles().stream()
                    .map(this::roleToDto)
                    .collect(Collectors.toSet());
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .roles(roles)
                .enabled(user.isEnabled())
                .accountNonLocked(user.isAccountNonLocked())
                .build();
    }

    public User toDomain(UserCreateDto userResponseDto) {
        if (userResponseDto == null) {
            return null;
        }
        Set<Role> roles = new HashSet<>();

        if (userResponseDto.getRoles() == null) {
        } else {
            roles = userResponseDto.getRoles().stream()
                    .map(roleName -> Role.builder()
                            .authority(RoleType.valueOf(String.valueOf(roleName)))
                            .build())
                    .collect(Collectors.toSet());
        }

        return User.builder()
                .email(userResponseDto.getEmail())
                .username(userResponseDto.getUsername())
                .password(userResponseDto.getPassword())
                .roles(roles)
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }

    public User toDomain(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        Set<Role> roles = userDto.getRoles().stream()
                .map(this::roleToDomain)
                .collect(Collectors.toSet());

        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .roles(roles)
                .enabled(userDto.getEnabled() != null ? userDto.getEnabled() : true)
                .accountNonLocked(userDto.getAccountNonLocked() != null ? userDto.getAccountNonLocked() : true)
                .build();
    }

    private RoleDto roleToDto(Role role) {
        if (role == null) {
            return null;
        }

        return RoleDto.builder()
                .authority(role.getAuthority().name())
                .build();
    }

    private Role roleToDomain(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }

        return Role.builder()
                .authority(RoleType.valueOf(roleDto.getAuthority()))
                .build();
    }
}
