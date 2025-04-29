package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Product;

import org.springframework.data.domain.Page;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    // Método para buscar productos por nombre con paginación y ordenación
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(UUID id);
}
