package com.juanma.proyecto_vn.domain.service;

import com.juanma.proyecto_vn.domain.model.Product;

import java.util.Optional;

/**
 * Puerto de salida (secundario) para el caché de productos
 */
public interface IProductCacheService {
    Optional<Product> getFromCache(String key);

    void saveInCache(String key, Product product);

    void removeFromCache(String key);
}