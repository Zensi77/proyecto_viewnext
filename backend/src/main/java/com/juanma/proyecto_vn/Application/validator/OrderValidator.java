package com.juanma.proyecto_vn.Application.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Order;
import com.juanma.proyecto_vn.domain.model.OrderItem;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.repository.ProductRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.NoStockException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Validador para órdenes con lógica de negocio compleja
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderValidator {

    private final ProductRepository productRepository;

    // Cache para evitar múltiples consultas al mismo producto en la misma sesión
    private final Map<UUID, Product> productCache = new ConcurrentHashMap<>();

    /**
     * Valida una orden completa y sus productos
     * 
     * @param order Orden a validar
     * @return Lista de errores encontrados
     */
    public List<String> validateOrder(Order order) {
        List<String> errors = new ArrayList<>();

        if (order == null) {
            errors.add("La orden no puede ser nula");
            return errors;
        }

        if (order.getTotalPrice() == null || order.getTotalPrice() <= 0) {
            errors.add("El precio total debe ser mayor que cero");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            errors.add("La orden debe contener al menos un producto");
            return errors; // No se pueden validar los items si no hay ninguno
        }

        // Validar cada item individualmente
        double calculatedTotal = 0;
        for (OrderItem item : order.getItems()) {
            errors.addAll(validateOrderItem(item));

            // Calcular el subtotal usando el producto del repositorio
            if (item.getProduct().getId()!= null) {
                Product product = getProduct(item.getProduct().getId());
                if (product != null) {
                    calculatedTotal += product.getPrice() * item.getQuantity();
                }
            }
        }

        // Verificar si el total calculado coincide aproximadamente con el total
        // proporcionado
        // Permitimos una pequeña diferencia por posibles errores de redondeo
        if (Math.abs(calculatedTotal - order.getTotalPrice()) > 0.01) {
            errors.add("El precio total no coincide con la suma de los productos: calculado="
                    + calculatedTotal + ", proporcionado=" + order.getTotalPrice());
        }

        return errors;
    }

    /**
     * Valida un item individual de la orden
     * 
     * @param item Item a validar
     * @return Lista de errores encontrados
     */
    private List<String> validateOrderItem(OrderItem item) {
        List<String> errors = new ArrayList<>();

        if (item == null) {
            errors.add("El item de la orden no puede ser nulo");
            return errors;
        }

        if (item.getProduct().getId() == null) {
            errors.add("El ID del producto no puede ser nulo");
            return errors;
        }

        if (item.getQuantity() <= 0) {
            errors.add("La cantidad debe ser mayor que cero para el producto " + item.getProduct().getId());
        }

        if (item.getQuantity() > 100) {
            errors.add("La cantidad no puede ser mayor que 100 unidades para el producto " + item.getProduct().getId());
        }

        // Verificar que el producto existe y tiene stock suficiente
        Product product = getProduct(item.getProduct().getId());
        if (product == null) {
            errors.add("El producto con ID " + item.getProduct().getId() + " no existe");
        } else if (product.getStock() < item.getQuantity()) {
            errors.add("No hay stock suficiente para el producto " + product.getName()
                    + " (solicitado: " + item.getQuantity() + ", disponible: " + product.getStock() + ")");
        }

        return errors;
    }

    /**
     * Verifica el stock disponible para un item de pedido
     * Lanza una excepción si no hay suficiente stock
     * 
     * @param item Item a verificar
     */
    public void checkStockAvailability(OrderItem item) {
        Product product = getProduct(item.getProduct().getId());

        if (product == null) {
            throw new NoStockException("El producto con ID " + item.getProduct().getId() + " no existe");
        }

        if (product.getStock() < item.getQuantity()) {
            throw new NoStockException("No hay stock suficiente para el producto " + product.getName()
                    + " (solicitado: " + item.getQuantity() + ", disponible: " + product.getStock() + ")");
        }
    }

    /**
     * Obtiene un producto por su ID, utilizando cache para mejorar rendimiento
     * 
     * @param productId ID del producto
     * @return Producto encontrado o null
     */
    private Product getProduct(UUID productId) {
        return productCache.computeIfAbsent(productId, id -> {
            Product product = productRepository.findById(id);
            if (product == null || product.getPrice() == null) {
                log.warn("Product with ID {} is null or has no price", id);
                return null;
            }
            return product;
        });
    }

    /**
     * Limpia la caché de productos
     */
    public void clearCache() {
        productCache.clear();
    }
}