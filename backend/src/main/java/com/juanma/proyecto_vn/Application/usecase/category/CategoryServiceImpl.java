package com.juanma.proyecto_vn.Application.usecase.category;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.repository.CategoryRepository;
import com.juanma.proyecto_vn.domain.service.ICategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        log.debug("Creando categoría en BD: {}", category);
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        log.debug("Buscando todas las categorías en BD");
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(UUID id) {
        log.debug("Buscando categoría por ID en BD: {}", id);
        return categoryRepository.findById(id);
    }

    @Override
    public Category updateCategory(UUID id, Category category) {
        log.debug("Actualizando categoría en BD: {}", category);

        return categoryRepository.update(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        log.debug("Eliminando categoría por ID en BD: {}", id);
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("Verificando si existe categoría por nombre en BD: {}", name);
        return categoryRepository.findByName(name) != null;

    }

}
