package com.juanma.proyecto_vn.models;

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
    @Column(name = "updated_at")
    @CreationTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    @UpdateTimestamp
    private LocalDateTime createdAt;

}
