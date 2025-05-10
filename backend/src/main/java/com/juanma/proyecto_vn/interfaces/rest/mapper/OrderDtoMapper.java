package com.juanma.proyecto_vn.interfaces.rest.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Order;
import com.juanma.proyecto_vn.domain.model.OrderItem;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.CreateOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.GetOrderDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.order.ProductOrderDto;

import lombok.RequiredArgsConstructor;

/**
 * Mapeador para convertir entre modelos de dominio y DTOs para la API REST
 */
@Component
@RequiredArgsConstructor
public class OrderDtoMapper {

    private final ProductDtoMapper productDtoMapper;

    public GetOrderDto toDto(Order order){
        List<ProductOrderDto> productOrderDtos = order.getItems().stream()
                .map(productOrder -> ProductOrderDto.builder()
                        .product(productOrder.getProduct())
                        .quantity(productOrder.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return GetOrderDto.builder()
                .id(order.getId())
                .items(productOrderDtos)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public Order toDomain(CreateOrderDto orderDto) {
        List<OrderItem> items = new ArrayList<>();
        int index = 0;
        for (ProductOrderDto productOrder : orderDto.getProductOrder()) {
            items.add(OrderItem.builder()
                    .product(orderDto.getProductOrder().get(index).getProduct())
                    .quantity(productOrder.getQuantity())
                    .build());

            index++;
        }
        return Order.builder()
                .items(items)
<<<<<<< HEAD
                .paymentMethod(orderDto.getPaymentMethod())
                .build();
    }

=======
                .paymentMethod(dto.getPaymentMethod())
                .build();
    }

    /**
     * Mapea un OrderItem a un GetProductOrderDto
     */
    private GetProductOrderDto mapOrderItemToDto(OrderItem item) {
        return GetProductOrderDto.builder()
                .product(productDtoMapper.toDto(Product.builder()
                        .id(item.getProduct()
                                .getId())
                        .name(item.getProduct()
                                .getName())
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
                .product(Product.builder()
                        .id(dto.getProductId())
                        .build())
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
>>>>>>> e5c27f731afd3e7b1d3fa7f76a138056b3eb3479
}
