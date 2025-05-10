package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SoftDelete;

@Data
@MappedSuperclass // Simboliza que esta clase es una superclase de entidad y no se debe mapear a
                  // una tabla
@SoftDelete // Indica que esta entidad es una entidad de borrado suave
public abstract class BaseEntity {
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
