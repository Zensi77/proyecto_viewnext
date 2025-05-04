package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Order;

/**
 * Puerto primario (entrada) para los casos de uso relacionados con pedidos
 */
public interface IOrderService {
    /**
     * Obtiene todos los pedidos de un usuario
     * 
     * @param email Email del usuario
     * @return Lista de pedidos del usuario
     */
    List<Order> getAllOrders(String email);

    /**
     * Obtiene un pedido específico de un usuario
     * 
     * @param email   Email del usuario
     * @param orderId ID del pedido
     * @return El pedido solicitado
     */
    Order getOrderById(String email, UUID orderId);

    /**
     * Crea un nuevo pedido
     * 
     * @param order Datos del pedido a crear
     * @param email Email del usuario
     * @return El pedido creado
     */
    Order createOrder(Order order, String email);

    /**
     * Cancela un pedido existente
     * 
     * @param orderId ID del pedido a cancelar
     * @param email   Email del usuario
     */
    void cancelOrder(UUID orderId, String email);

    /**
     * Elimina un pedido existente
     * 
     * @param orderId ID del pedido a eliminar
     * @param email   Email del usuario
     */
    void deleteOrder(UUID orderId, String email);
}
