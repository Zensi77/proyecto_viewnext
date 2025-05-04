package com.juanma.proyecto_vn.domain.repository;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

/**
 * Puerto de salida (secundario) para la persistencia de productos
 */
public interface ProductRepository {
    Page<Product> getAllProducts(int page, int size, String sortBy, String orderBy,
            Map<String, Object> filters);

    Page<Product> findByNameContaining(String name, int page, int size, String sortBy, String orderBy);

    Page<Product> findByCategoryName(List<String> categoryIds, int page, int size, String sortBy, String orderBy);

    Page<Product> findByProviderName(List<String> providerIds, int page, int size, String sortBy, String orderBy);

    Page<Product> findByPriceBetween(String minPrice, String maxPrice, int page, int size, String sortBy,
            String orderBy);

    Specification<ProductEntity> getProductSpecification(Map<String, Object> filters);

    long count();

    Product findById(UUID id);

    Product save(Product product);

    void delete(Product product);

    void deleteById(UUID id);

    List<Product> findAll();

    List<Map<String, Object>> findAllNames();
}