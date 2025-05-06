package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.juanma.proyecto_vn.domain.model.Cart;
import com.juanma.proyecto_vn.domain.model.CartItem;
import com.juanma.proyecto_vn.domain.repository.CartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductCartEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.CartMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.ProductCartMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaCartRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador de repositorio que implementa la interfaz CartRepository
 * usando la persistencia JPA y los nuevos mappers optimizados
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CartRepositoryAdapter implements CartRepository {

    private final JpaCartRepository jpaCartRepository;
    private final CartMapper cartMapper;
    private final ProductCartMapper productCartMapper;

    /**
     * Guarda un carrito en la base de datos
     * 
     * @param cart El carrito a guardar
     * @return El carrito guardado
     */
    @Override
    @Transactional
    public Cart save(Cart cart) {
        log.debug("Guardando carrito: {}", cart);

        // 1. Convertir el carrito básico sin items
        CartEntity cartEntity = cartMapper.toEntity(cart);

        // 2. Si es una actualización, buscar la entidad existente
        boolean isUpdate = cart.getId() != null;
        if (isUpdate) {
            jpaCartRepository.findById(UUID.fromString(cart.getId()))
                    .ifPresent(existingCart -> {
                        cartEntity.setId(existingCart.getId());
                        cartEntity.setProductCart(existingCart.getProductCart());
                    });
        }

        // 3. Guardar la entidad para obtener un ID si es nueva
        CartEntity savedCartEntity = jpaCartRepository.save(cartEntity);

        // 4. Actualizar los items del carrito
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            // Borrar items actuales
            savedCartEntity.getProductCart().clear();

            // Agregar los nuevos items
            for (CartItem item : cart.getItems()) {
                ProductCartEntity productCartEntity = productCartMapper.toEntity(item, savedCartEntity);
                savedCartEntity.addProductCart(productCartEntity);
            }

            // Guardar los cambios
            savedCartEntity = jpaCartRepository.save(savedCartEntity);
        }

        // 5. Convertir el resultado a un modelo de dominio completo
        Cart result = cartMapper.toDomain(savedCartEntity);

        // 6. Añadir los items al modelo de dominio
        if (savedCartEntity.getProductCart() != null) {
            result.setItems(savedCartEntity.getProductCart().stream()
                    .map(productCartEntity -> productCartMapper.toDomain(productCartEntity, result))
                    .collect(Collectors.toList()));
        }

        log.debug("Carrito guardado: {}", result);
        return result;
    }

    /**
     * Busca un carrito por el ID del usuario
     * 
     * @param userId El ID del usuario
     * @return El carrito encontrado o null si no existe
     */
    @Override
    public Cart findByUserId(UUID userId) {
        log.debug("Buscando carrito para el usuario con ID: {}", userId);

        return jpaCartRepository.findByUserId(userId)
                .map(cartEntity -> {
                    // Convertir a modelo de dominio básico
                    Cart cart = cartMapper.toDomain(cartEntity);

                    // Añadir los items
                    if (cartEntity.getProductCart() != null) {
                        cart.setItems(cartEntity.getProductCart().stream()
                                .map(productCartEntity -> productCartMapper.toDomain(productCartEntity, cart))
                                .collect(Collectors.toList()));
                    }

                    log.debug("Carrito encontrado: {}", cart);
                    return cart;
                })
                .orElse(null);
    }
}
