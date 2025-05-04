package com.juanma.proyecto_vn.interfaces.rest.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Order;
import com.juanma.proyecto_vn.domain.model.OrderItem;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateProductOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetProductOrderDto;

import lombok.RequiredArgsConstructor;

/**
 * Mapeador para convertir entre modelos de dominio y DTOs para la API REST
 */
@Component
@RequiredArgsConstructor
public class OrderDtoMapper {

    private final ProductDtoMapper productDtoMapper;

    /**
     * Convierte un modelo de dominio a un DTO de respuesta
     */
    public GetOrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        List<GetProductOrderDto> productOrderDtos = new ArrayList<>();
        if (order.getItems() != null) {
            productOrderDtos = order.getItems().stream()
                    .map(this::mapOrderItemToDto)
                    .collect(Collectors.toList());
        }

        return GetOrderDto.builder()
                .id(order.getId().toString())
                .productOrder(productOrderDtos)
                .total_price(order.getTotalPrice())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .build();
    }

    /**
     * Convierte un DTO de creación a un modelo de dominio
     */
    public Order toDomain(CreateOrderDto dto) {
        if (dto == null) {
            return null;
        }

        List<OrderItem> items = new ArrayList<>();
        if (dto.getProductOrder() != null) {
            items = dto.getProductOrder().stream()
                    .map(this::mapDtoToOrderItem)
                    .collect(Collectors.toList());
        }

        return Order.builder()
                .id(UUID.randomUUID()) // Se genera un nuevo ID para el pedido
                .items(items)
                .totalPrice(dto.getTotal_price())
                .status(dto.getStatus())
                .paymentMethod(dto.getPaymentMethod())
                .build();
    }

    /**
     * Mapea un OrderItem a un GetProductOrderDto
     */
    private GetProductOrderDto mapOrderItemToDto(OrderItem item) {
        return GetProductOrderDto.builder()
                .product(productDtoMapper.toDto(Product.builder()
                        .id(item.getProductId())
                        .build()))
                .quantity(item.getQuantity())
                .build();
    }

    /**
     * Mapea un CreateProductOrderDto a un OrderItem
     */
    private OrderItem mapDtoToOrderItem(CreateProductOrderDto dto) {
        // El producto completo se establecerá más tarde en la capa de aplicación
        // cuando se recupere de la base de datos
        return OrderItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .build();
    }

    /**
     * Convierte una lista de modelos de dominio a una lista de DTOs
     */
    public List<GetOrderDto> toDtoList(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }

        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
