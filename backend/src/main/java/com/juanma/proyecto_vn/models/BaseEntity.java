package com.juanma.proyecto_vn.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.util.Date;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Data
@MappedSuperclass // Simboliza que esta clase es una superclase de entidad y no se debe mapear a
                  // una tabla
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
public abstract class BaseEntity {
    @Column(name = "updated_at", nullable = false)
    private String updatedAt = new Date().toString();

    @Column(name = "created_at", nullable = false, updatable = false)
    private String createdAt = new Date().toString();

}
