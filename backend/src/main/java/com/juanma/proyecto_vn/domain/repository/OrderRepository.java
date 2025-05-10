package com.juanma.proyecto_vn.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Order;

/**
 * Puerto secundario (salida) para la persistencia de pedidos
 */
public interface OrderRepository {
    /**
     * Guarda un pedido en la base de datos
     * 
     * @param order Pedido a guardar
     * @return Pedido guardado
     */
    Order save(Order order);

    /**
     * Busca todos los pedidos de un usuario
     * 
     * @param userId ID del usuario
     * @return Lista de pedidos
     */
    List<Order> findAllByUserId(UUID userId);

    /**
     * Busca un pedido por su ID
     * 
     * @param id ID del pedido
     * @return Pedido encontrado o vacío
     */
    Optional<Order> findById(UUID id);

    Order cancelOrder(UUID orderId);

    /**
     * Elimina un pedido
     * 
     * @param order Pedido a eliminar
     */
    void delete(Order order);
}