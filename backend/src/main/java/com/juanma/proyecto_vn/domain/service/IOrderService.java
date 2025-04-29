package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetOrderDto;

public interface IOrderService {
    List<GetOrderDto> getAllOrders(String email);

    GetOrderDto getOrderById(String email, UUID orderId);

    GetOrderDto createOrder(CreateOrderDto order, String email);

    void cancelOrder(UUID orderId, String email);

    void deleteOrder(UUID orderId, String email);

}
