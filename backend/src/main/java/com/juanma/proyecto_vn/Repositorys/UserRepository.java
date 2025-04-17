package com.juanma.proyecto_vn.Repository;

import com.juanma.proyecto_vn.Dtos.UserCreateDto;
import com.juanma.proyecto_vn.Dtos.UserResponseDto;
import com.juanma.proyecto_vn.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    UserResponseDto findByEmail(String email);

    Optional<User> findById(UUID id);

    UserCreateDto save(UserCreateDto user);

    void deleteById(UUID id);


}
