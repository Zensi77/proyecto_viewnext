package com.juanma.proyecto_vn.Repositorys;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juanma.proyecto_vn.models.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);

    List<Category> findAll();

    @Query("SELECT c FROM Category c WHERE c.isDeleted = false AND c.id = :id")
    Optional<Category> findById(UUID id);

    @Query(value = "SELECT * FROM category WHERE id = :id", nativeQuery = true)
    Optional<Category> findByIdNative(@Param("id") String id);
}
