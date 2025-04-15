package com.juanma.proyecto_vn.models;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass // Simboliza que esta clase es una superclase de entidad y no se debe mapear a una tabla
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
public abstract class BaseEntity {
}
