package com.juanma.proyecto_vn.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.User;

/**
 * Puerto secundario (salida) para la persistencia de usuarios
 */
public interface UserRepository {
    /**
     * Busca todos los usuarios
     *
     * @return Lista de usuarios
     */
    List<User> findAll();

    /**
     * Busca un usuario por su email
     * 
     * @param email Email del usuario
     * @return Usuario encontrado o vacío
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su ID
     * 
     * @param id ID del usuario
     * @return Usuario encontrado o vacío
     */
    Optional<User> findById(UUID id);

    /**
     * Guarda un usuario
     * 
     * @param user Usuario a guardar
     * @param isAdmin Indica si el usuario es administrador
     * @return Usuario guardado
     */
    User save(User user, boolean isAdmin);

    User modify(User user);
}