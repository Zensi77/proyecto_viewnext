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

        public GetOrderDto toDto(Order order) {
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
                                .paymentMethod(orderDto.getPaymentMethod())
                                .build();
        }

}
