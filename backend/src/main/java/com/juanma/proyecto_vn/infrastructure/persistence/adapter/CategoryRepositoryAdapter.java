package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.repository.CategoryRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.CategoryEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.CategoryMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaCategoryRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Adaptador del repositorio de categorías que implementa el puerto de salida
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpaCategoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        log.debug("Guardando categoría en BD: {}", category);
        CategoryEntity entity = categoryMapper.toEntity(category);
        CategoryEntity savedEntity = jpaCategoryRepository.save(entity);
        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public List<Category> findAll() {
        log.debug("Buscando todas las categorías en BD");
        List<CategoryEntity> entities = jpaCategoryRepository.findAll();
        return entities.stream()
                .map(categoryMapper::toDomain)
                .toList();
    }

    @Override
    public Category findById(UUID id) {
        log.debug("Buscando categoría por ID en BD: {}", id);
        Category cat = jpaCategoryRepository.findById(id)
                .map(categoryMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return cat;
    }

    @Override
    public Category findByName(String name) {
        log.debug("Buscando categoría por nombre en BD: {}", name);
        CategoryEntity entity = jpaCategoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return categoryMapper.toDomain(entity);
    }

    @Override
    public Category update(Category category) {
        log.debug("Actualizando categoría en BD: {}", category);

        CategoryEntity existingEntity = jpaCategoryRepository.findById(category.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // Actualizar los campos de la entidad existente con los nuevos valores
        existingEntity.setName(category.getName());
        CategoryEntity updatedEntity = jpaCategoryRepository.save(existingEntity);
        return categoryMapper.toDomain(updatedEntity);

    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Eliminando categoría por ID en BD: {}", id);
        jpaCategoryRepository.deleteById(id);
    }
}