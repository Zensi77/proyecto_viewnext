package com.juanma.proyecto_vn.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@Data
@AllArgsConstructor
@SQLDelete(sql = "UPDATE almacen SET is_deleted = true WHERE id_almacen = ?")
@Where(clause = "is_deleted = false")
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

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "id")
    private Provider provider;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "id")
    private Category category;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

}
