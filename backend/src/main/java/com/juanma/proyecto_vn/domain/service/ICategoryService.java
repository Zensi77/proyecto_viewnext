package com.juanma.proyecto_vn.domain.service;

import com.juanma.proyecto_vn.domain.model.Category;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para casos de uso de categorías
 */
public interface ICategoryService {

    /**
     * Crea una nueva categoría
     * 
     * @param category La categoría a crear
     * @return La categoría creada
     */
    Category createCategory(Category category);

    /**
     * Obtiene todas las categorías
     * 
     * @return Lista de categorías
     */
    List<Category> getAllCategories();

    /**
     * Busca una categoría por su ID
     * 
     * @param id El ID de la categoría
     * @return La categoría si existe
     */
    Category getCategoryById(UUID id);

    /**
     * Actualiza una categoría existente
     * 
     * @param id       El ID de la categoría a actualizar
     * @param category Los nuevos datos de la categoría
     * @return La categoría actualizada
     */
    Category updateCategory(UUID id, Category category);

    /**
     * Elimina una categoría por su ID
     * 
     * @param id El ID de la categoría a eliminar
     */
    void deleteCategory(UUID id);

    /**
     * Verifica si una categoría ya existe por nombre
     * 
     * @param name El nombre a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);
}
