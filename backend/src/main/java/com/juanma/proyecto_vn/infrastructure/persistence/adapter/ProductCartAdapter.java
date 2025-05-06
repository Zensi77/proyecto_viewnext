package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.domain.repository.ProductCartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.CartMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.ProductCartMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaCartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaProductCartRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador para la gestión de los items del carrito
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCartAdapter implements ProductCartRepository {
    private final JpaProductCartRepository jpaProductCartRepository;
    private final JpaCartRepository jpaCartRepository;
    private final ProductCartMapper productCartMapper;
    private final CartMapper cartMapper;

    /**
     * Guarda un item en el carrito
     * 
     * @param cartItem El item a guardar
     * @return El item guardado
     */
    @Override
    @Transactional
    public CartItem save(CartItem cartItem) {
        if (cartItem == null || cartItem.getCart() == null || cartItem.getCart().getId() == null) {
            throw new IllegalArgumentException("El item del carrito y su referencia al carrito son obligatorios");
        }

        log.debug("Guardando item en el carrito: {}", cartItem);

        // Obtener la entidad CartEntity
        CartEntity cartEntity = jpaCartRepository.findById(UUID.fromString(cartItem.getCart().getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        // Convertir a ProductCartEntity
        ProductCartEntity productCartEntity = productCartMapper.toEntity(cartItem, cartEntity);

        // Guardar la entidad
        ProductCartEntity savedEntity = jpaProductCartRepository.save(productCartEntity);

        // Convertir de vuelta a dominio
        Cart cart = cartMapper.toDomain(cartEntity);
        CartItem savedItem = productCartMapper.toDomain(savedEntity, cart);

        log.debug("Item guardado en el carrito: {}", savedItem);
        return savedItem;
    }

    /**
     * Busca un item en el carrito por el ID del producto y el ID del carrito
     * 
     * @param productId ID del producto
     * @param cartId    ID del carrito
     * @return El item encontrado
     */
    @Override
    public CartItem findByProductAndCart(UUID productId, UUID cartId) {
        log.debug("Buscando item en el carrito para producto: {} y carrito: {}", productId, cartId);

        return jpaProductCartRepository.findByProductAndCart(productId, cartId)
                .map(entity -> {
                    // Obtener el carrito básico
                    Cart cart = cartMapper.toDomain(entity.getCart());

                    // Convertir a modelo de dominio
                    CartItem item = productCartMapper.toDomain(entity, cart);

                    log.debug("Item encontrado: {}", item);
                    return item;
                })
                .orElseThrow(() -> {
                    log.warn("Item no encontrado en el carrito para producto: {} y carrito: {}", productId, cartId);
                    return new ResourceNotFoundException("Producto no encontrado en el carrito");
                });
    }

    /**
     * Elimina un item del carrito por el ID del producto y el ID del carrito
     * 
     * @param productId ID del producto
     * @param cartId    ID del carrito
     */
    @Override
    @Transactional
    public void deleteByProductAndCart(UUID productId, UUID cartId) {
        log.debug("Eliminando item del carrito para producto: {} y carrito: {}", productId, cartId);
        jpaProductCartRepository.deleteByProductAndCart(productId, cartId);
        log.debug("Item eliminado del carrito");
    }
}
