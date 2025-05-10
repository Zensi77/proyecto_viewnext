package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Order;
import com.juanma.proyecto_vn.domain.model.OrderItem;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.OrderEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductOrderEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.UserEntity;

import lombok.RequiredArgsConstructor;

/**
 * Mapper para convertir entre entidades JPA y modelos de dominio para pedidos
 */
@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        List<OrderItem> items = new ArrayList<>();
        if (entity.getProductOrder() != null) {
            items = entity.getProductOrder().stream()
                    .map(this::mapProductOrderToDomain)
                    .collect(Collectors.toList());
        }

        return Order.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .items(items)
                .totalPrice(entity.getTotal_price())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public OrderEntity toEntity(Order domain) {
        if (domain == null) {
            return null;
        }

        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .user(UserEntity.builder().id(domain.getUserId()).build())
                .total_price(domain.getTotalPrice())
                .status(domain.getStatus() != null ? domain.getStatus() : "PENDING")
                .paymentMethod(domain.getPaymentMethod())
                .build();

        if (domain.getItems() != null && !domain.getItems().isEmpty()) {
            ProductOrderEntity.ProductOrderPK pk = new ProductOrderEntity.ProductOrderPK();

            List<ProductOrderEntity> productOrders = domain.getItems().stream()
                    .map(item -> {
                        pk.setOrderId(entity.getId());
                        pk.setProductId(item.getProduct().getId());
                        return ProductOrderEntity.builder()
                                .id(pk)
                                .order(entity)
                                .product(ProductEntity.builder()
                                        .id(item.getProduct().getId())
                                        .build())
                                .quantity(item.getQuantity())
                                .build();
                    })
                    .collect(Collectors.toList());
            entity.setProductOrder(productOrders);
        } else {
            entity.setProductOrder(new ArrayList<>());
        }

        return entity;
    }

    /**
     * Mapea un ProductOrderEntity a un OrderItem del dominio
     */
    private OrderItem mapProductOrderToDomain(ProductOrderEntity productOrder) {
        return OrderItem.builder()
                .product(productMapper.toDomain(productOrder.getProduct()))
                .quantity(productOrder.getQuantity())
                .build();
    }

}