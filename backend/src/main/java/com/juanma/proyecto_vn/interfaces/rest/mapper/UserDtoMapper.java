package com.juanma.proyecto_vn.interfaces.rest.mapper;


import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}
