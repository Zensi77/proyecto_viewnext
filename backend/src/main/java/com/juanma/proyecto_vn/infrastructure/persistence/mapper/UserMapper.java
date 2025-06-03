package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.UserEntity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Mapper para convertir entre entidades JPA y modelos de dominio para usuarios
 */
@Component
public class UserMapper {

    @Autowired
    private ProductMapper productMapper;

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoles())
                .wishlists(entity.getWishlists() != null ? entity.getWishlists().stream()
                        .map(productMapper::toDomain)
                        .toList() : Collections.emptyList())
                .enabled(entity.isEnabled())
                .accountNonLocked(entity.isAccountNonLocked())
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .username(domain.getUsername())
                .roles(domain.getRoles())
                .wishlists(domain.getWishlists() != null ? domain.getWishlists().stream()
                        .map(productMapper::toEntity)
                        .toList() : Collections.emptyList())
                .enabled(domain.isEnabled())
                .accountNonLocked(domain.isAccountNonLocked())
                .build();
    }
}