package com.juanma.proyecto_vn.interfaces.rest.controller;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.service.ICategoryService;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.CategoryMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final ICategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /**
     * Método para validar errores en las peticiones
     * 
     * @param result Resultado de la validación
     * @return Mapa con los errores si existen
     */
    private ResponseEntity<Map<String, String>> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Crear una nueva categoría
     * 
     * @param categoryRequestDto DTO con los datos de la categoría
     * @param result             Resultado de la validación
     * @return DTO de respuesta con la categoría creada
     */
    @PostMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryResponseDto categoryRequestDto,
            BindingResult result) {
        log.info("Solicitud para crear nueva categoría: {}", categoryRequestDto);

        if (result.hasErrors()) {
            return validation(result);
        }

        try {
            // Convertir DTO a modelo de dominio
            Category category = categoryMapper.toDomain(categoryRequestDto);

            // Crear categoría
            Category createdCategory = categoryService.createCategory(category);

            // Convertir resultado a DTO de respuesta
            CategoryResponseDto response = categoryMapper.toDto(createdCategory);

            log.info("Categoría creada exitosamente: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Error al crear categoría: {}", e.getMessage());
            Map<String, String> errors = new HashMap<>();
            errors.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errors);
        }
    }

    /**
     * Obtener todas las categorías
     * 
     * @return Lista de DTOs de categorías
     */
    @GetMapping("/")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        log.info("Solicitud para obtener todas las categorías");

        List<Category> categories = categoryService.getAllCategories();
        List<CategoryResponseDto> response = categoryMapper.toDtoList(categories);

        log.info("Retornando {} categorías", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener categoría por ID
     * 
     * @param id ID de la categoría a buscar
     * @return DTO de la categoría encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable UUID id) {
        log.info("Solicitud para obtener categoría con ID: {}", id);

        Category category = categoryService.getCategoryById(id);

        CategoryResponseDto response = categoryMapper.toDto(category);

        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si existe un nombre de categoría
     * 
     * @param name Nombre a verificar
     * @return True si existe, False si no existe
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> categoryExists(@RequestParam String name) {
        log.info("Verificando existencia de categoría con nombre: {}", name);
        boolean exists = categoryService.existsByName(name);
        log.info("Resultado: categoría {} {}", name, exists ? "existe" : "no existe");
        return ResponseEntity.ok(exists);
    }

    /**
     * Actualizar una categoría existente
     * 
     * @param id                 ID de la categoría a actualizar
     * @param categoryRequestDto DTO con los nuevos datos
     * @param result             Resultado de la validación
     * @return DTO con la categoría actualizada
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryResponseDto categoryRequestDto,
            BindingResult result) {
        log.info("Solicitud para actualizar categoría con ID: {} - Nuevos datos: {}", id, categoryRequestDto);

        if (result.hasErrors()) {
            return validation(result);
        }

        try {
            // Convertir DTO a modelo de dominio
            Category category = categoryMapper.toDomain(categoryRequestDto);

            // Actualizar categoría
            Category updatedCategory = categoryService.updateCategory(id, category);

            // Convertir resultado a DTO de respuesta
            CategoryResponseDto response = categoryMapper.toDto(updatedCategory);

            log.info("Categoría actualizada exitosamente: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("Error al actualizar categoría: {}", e.getMessage());
            Map<String, String> errors = new HashMap<>();
            errors.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
    }

    /**
     * Eliminar una categoría
     * 
     * @param id ID de la categoría a eliminar
     * @return Respuesta vacía con código 204
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID id) {
        log.info("Solicitud para eliminar categoría con ID: {}", id);

        try {
            categoryService.deleteCategory(id);
            log.info("Categoría eliminada exitosamente: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.warn("Error al eliminar categoría: {}", e.getMessage());
            Map<String, String> errors = new HashMap<>();
            errors.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
    }
}