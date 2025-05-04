package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

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
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserEntity> userEntityOpt = jpaUserRepository.findByEmail(email);
        return userEntityOpt.map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        Optional<UserEntity> userEntityOpt = jpaUserRepository.findById(id);
        return userEntityOpt.map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedUserEntity = jpaUserRepository.save(userEntity);
        return userMapper.toDomain(savedUserEntity);
    }
}