package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartMapper {
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    public CartEntity toEntity(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartEntity.builder()
                .user(userMapper.toEntity(cart.getUser()))
                .productCart(mapProductOrderToEntity(cart.getItems()))
                .total_price(cart.getTotalPrice())
                .build();
    }

    public Cart toDomain(CartEntity entity) {
        if (entity == null) {
            return null;
        }

        return Cart.builder()
                .id(entity.getId().toString())
                .user(userMapper.toDomain(entity.getUser()))
                .items(mapProductOrderToDomain(entity.getProductCart()))
                .totalPrice(entity.getTotal_price())
                .build();
    }

    public List<CartItem> mapProductOrderToDomain(List<ProductCartEntity> productOrder) {
        if (productOrder == null || productOrder.isEmpty()) {
            return new ArrayList<>();
        }
        return productOrder.stream()
                .map(item -> CartItem.builder()
                        .product(productMapper.toDomain(item.getProduct()))
                        .quantity(item.getQuantity())
                        .build())
                .toList();
    }

    public List<ProductCartEntity> mapProductOrderToEntity(List<CartItem> productOrder) {
        if (productOrder == null || productOrder.isEmpty()) {
            return new ArrayList<>();

        }
        return productOrder.stream()
                .map(item -> {
                    ProductCartEntity.ProductCartPK pk = new ProductCartEntity.ProductCartPK();
                    pk.setProductId(item.getProduct().getId());
                    pk.setCartId(UUID.fromString(item.getCart().getId()));

                    return ProductCartEntity.builder()
                            .id(pk)
                            .product(productMapper.toEntity(item.getProduct()))
                            .cart(CartEntity.builder().id(UUID.fromString(item.getCart().getId())).build())
                            .quantity(item.getQuantity())
                            .build();
                })
                .toList();
    }
}
