package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad ProductEntity
 */
@Repository
public interface JpaProductRepository
        extends JpaRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {

    Page<ProductEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.id IN :id")
    Page<ProductEntity> findByCategoryIgnoreCase(List<UUID> id, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.provider.id IN :id")
    Page<ProductEntity> findByProviderIgnoreCase(List<UUID> id, Pageable pageable);

    @Query("SELECT p FROM ProductEntity p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<ProductEntity> findByPriceBetween(String minPrice, String maxPrice, Pageable pageable);

    @Query(value = "SELECT id, name FROM product", nativeQuery = true)
    List<Map<String, Object>> findAllNames();

    @Query(value = "SELECT p FROM ProductEntity p ORDER BY function('RAND') LIMIT :limit")
    List<ProductEntity> findRandom(int limit);
}