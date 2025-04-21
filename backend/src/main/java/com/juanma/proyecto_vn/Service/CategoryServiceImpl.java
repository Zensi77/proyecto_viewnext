package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Dtos.category.CreateCategoryDto;
import com.juanma.proyecto_vn.Exception.ResourceNotFoundException;
import com.juanma.proyecto_vn.Repositorys.CategoryRepository;
import com.juanma.proyecto_vn.interfaces.ICategoryService;
import com.juanma.proyecto_vn.models.Category;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<CreateCategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::getCategoryDto)
                .toList();
    }

    @Override
    public CreateCategoryDto getCategory(UUID id) {
        System.out.println("ID recibido: " + id.toString());

        Optional<Category> category = categoryRepository.findByIdNative(id.toString());

        if (category.isEmpty()) {
            throw new ResourceNotFoundException("Category not found");
        }

        return getCategoryDto(category.get());
    }

    @Override
    public CreateCategoryDto createCategory(CreateCategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return getCategoryDto(savedCategory);
    }

    @Override
    public CreateCategoryDto updateCategory(UUID id, CreateCategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        return getCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        categoryRepository.delete(category);
    }

    private CreateCategoryDto getCategoryDto(Category category) {
        return CreateCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}