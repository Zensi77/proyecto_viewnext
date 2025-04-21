package com.juanma.proyecto_vn.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.Dtos.category.CreateCategoryDto;
import com.juanma.proyecto_vn.interfaces.ICategoryService;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CreateCategoryDto>> getAllCategories() {
        List<CreateCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreateCategoryDto> getCategory(@PathVariable UUID id) {
        CreateCategoryDto category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CreateCategoryDto> createCategory(@RequestBody CreateCategoryDto category) {
        CreateCategoryDto createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateCategoryDto> updateCategory(@RequestBody CreateCategoryDto category, UUID id) {
        CreateCategoryDto updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}