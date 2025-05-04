package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProviderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad ProviderEntity
 */
@Repository
public interface JpaProviderRepository extends JpaRepository<ProviderEntity, UUID> {

    List<ProviderEntity> findByNameContaining(String name);
}