package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad CategoryEntity
 */
@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    /**
     * Busca una categoría por su nombre
     * 
     * @param name Nombre de la categoría
     * @return La categoría si existe
     */
    Optional<CategoryEntity> findByNameIgnoreCase(String name);

    /**
     * Busca una categoría por su nombre (sin ignorar mayúsculas y minúsculas)
     * 
     * @param name Nombre de la categoría
     * @return La categoría si existe
     */
    Optional<CategoryEntity> findByName(String name);
}