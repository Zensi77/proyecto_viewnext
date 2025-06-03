package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;

public interface IUserService {
    List<UserResponseDto> getAll();

    Map<String, Object> login(LoginDto user);

    Map<String, Object> saveUser(User user);

    Map<String, Object> saveAdmin(User user);

    Map<String, Object> updateUser(User user, UUID userId);

    boolean emailExist(String email);

    /**
     * Obtiene la lista de productos favoritos de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de productos favoritos
     */
    List<Product> getUserWishlist(UUID userId);

    /**
     * Añade un producto a la lista de favoritos de un usuario
     * 
     * @param userId    ID del usuario
     * @param productId ID del producto
     * @return true si se añadió correctamente, false si ya estaba en la lista
     */
    boolean addToWishlist(UUID userId, UUID productId);

    /**
     * Elimina un producto de la lista de favoritos de un usuario
     * 
     * @param userId    ID del usuario
     * @param productId ID del producto
     * @return true si se eliminó correctamente, false si no estaba en la lista
     */
    boolean removeFromWishlist(UUID userId, UUID productId);

    /**
     * Obtiene solo los IDs de los productos en la wishlist del usuario
     * Método optimizado para verificaciones de favoritos
     * 
     * @param userId ID del usuario
     * @return Set con los IDs de los productos favoritos
     */
    java.util.Set<UUID> getUserWishlistIds(UUID userId);
}
