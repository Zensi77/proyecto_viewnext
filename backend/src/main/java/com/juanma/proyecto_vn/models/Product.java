package com.juanma.proyecto_vn.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@AllArgsConstructor
@Data
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@FilterDef(name = "deletedFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public class Product {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "price", nullable = false)
    @Min(0)
    private Long price;

    @Column(name = "image", nullable = false)
    private String image;

    @ManyToOne(cascade = { CascadeType.ALL }) // Hace que se elimine el producto si se elimina el proveedor
    @JoinColumn(name = "id")
    private Provider provider;

    @ManyToOne(cascade = { CascadeType.ALL }) // Hace que se elimine el producto si se elimina la categoria
    @JoinColumn(name = "id")
    private Category category;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

}
