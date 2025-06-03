package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.RoleType;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaRoleRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.UserEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.UserMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador que implementa el puerto de repositorio de usuarios conectando con
 * JPA
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final JpaRoleRepository jpaRoleRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        List<UserEntity> userEntities = jpaUserRepository.findAll();
        return userEntities.stream().map(userMapper::toDomain).toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> userEntityOpt = jpaUserRepository.findByEmail(email);
        if (userEntityOpt.isEmpty()) {
            return null;
        }
        return userEntityOpt.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        Optional<UserEntity> userEntityOpt = jpaUserRepository.findById(id);
        if (userEntityOpt.isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        return userEntityOpt.map(userMapper::toDomain);
    }

    @Override
    public User save(User user, boolean isAdmin) {
        UserEntity userEntity = userMapper.toEntity(user);

        userEntity.setWishlists(new ArrayList<>());
        if (isAdmin) {
            userEntity.getRoles().add(jpaRoleRepository.findByAuthority(RoleType.ROLE_ADMIN));
        } else {
            userEntity.getRoles().add(jpaRoleRepository.findByAuthority(RoleType.ROLE_USER));
        }
        UserEntity savedUserEntity = jpaUserRepository.save(userEntity);
        return userMapper.toDomain(savedUserEntity);
    }

    @Override
    public User modify(User user) {
        UserEntity userEntity = jpaUserRepository.findById(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            userEntity.getRoles().clear();
            for (Role rol : user.getRoles()) {
                Role role = jpaRoleRepository.findByAuthority(rol.getAuthority());
                if (role != null) {
                    userEntity.getRoles().add(role);
                }
            }
        }
        userEntity.setEmail(user.getEmail());
        userEntity.setUsername(user.getUsername());
        if (user.getPassword() != null) {
            userEntity.setPassword(user.getPassword());
        }
        userEntity.setEnabled(user.isEnabled());
        userEntity.setAccountNonLocked(user.isAccountNonLocked());

        // Actualizar la wishlist
        if (user.getWishlists() != null) {
            userEntity.setWishlists(new ArrayList<>(userMapper.toEntity(user).getWishlists()));
        }

        UserEntity savedUserEntity = jpaUserRepository.save(userEntity);
        return userMapper.toDomain(savedUserEntity);
    }
}