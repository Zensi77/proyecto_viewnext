package com.juanma.proyecto_vn.Repositorys;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

import com.juanma.proyecto_vn.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Método para buscar productos por nombre con paginación y ordenación
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(UUID id);

    @Query(value = "SELECT * FROM products ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Product> findRandomProducts(int quantity);
}
