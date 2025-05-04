package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Product;

/**
 * Puerto primario (entrada) para los casos de uso relacionados con productos
 */
public interface IProductService {
        /**
         * Obtiene todos los productos con paginación y filtros
         * 
         * @param page    número de página
         * @param size    tamaño de página
         * @param sortBy  campo por el que ordenar
         * @param orderBy dirección de ordenamiento (asc/desc)
         * @param filters mapa con los filtros a aplicar (name, category, provider,
         *                priceMin, priceMax)
         * @return Mapa con datos de paginación y lista de productos
         */
        Map<String, Object> getAllProducts(int page, int size, String sortBy, String orderBy,
                        Map<String, Object> filters);

        /**
         * Obtiene una lista de nombres e IDs de productos
         */
        List<Map<String, String>> getAllNames();

        /**
         * Obtiene un producto por su ID
         */
        Product getProductById(UUID id, String userId);

        /**
         * Crea un nuevo producto
         */
        Product createProduct(String name, double price, String image, int stock, String description,
                        UUID categoryId, UUID providerId);

        /**
         * Obtiene productos aleatorios
         */
        List<Product> getRandomProducts(int quantity);

        /**
         * Actualiza un producto existente
         */
        Product updateProduct(UUID id, String name, double price, String image, int stock,
                        String description, UUID categoryId, UUID providerId);

        /**
         * Elimina un producto por su ID
         */
        Product deleteProduct(UUID id);
}
