package com.juanma.proyecto_vn.domain.repository;

import com.juanma.proyecto_vn.domain.model.Category;

import java.util.List;
import java.util.UUID;


/**
 * Puerto de salida para la persistencia de categorías
 */
public interface CategoryRepository {

    /**
     * Guarda una categoría
     * 
     * @param category La categoría a guardar
     * @return La categoría guardada con su ID generado
     */
    Category save(Category category);

    /**
     * Obtiene todas las categorías
     * 
     * @return Lista de categorías
     */
    List<Category> findAll();

    /**
     * Busca una categoría por su ID
     * 
     * @param id El ID de la categoría
     * @return La categoría encontrada o vacío
     */
    Category findById(UUID id);

    /**
     * Busca una categoría por su nombre
     * 
     * @param name El nombre de la categoría
     * @return La categoría encontrada o vacío
     */
    Category findByName(String name);

    /**
     * Actualiza una categoría existente
     * 
     * @param category La categoría con los datos actualizados
     * @return La categoría actualizada
     */
    Category update(Category category);

    /**
     * Elimina una categoría por su ID
     * 
     * @param id El ID de la categoría a eliminar
     */
    void deleteById(UUID id);
}