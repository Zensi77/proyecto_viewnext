package com.juanma.proyecto_vn.models;

import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Filter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@Builder
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@Filter(name = "deletedFilter", condition = "is_deleted = :isDeleted")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(10,2)")
    private double price;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "id_provider")
    private Provider provider;

    @ManyToOne()
    @JoinColumn(name = "id_category")
    private Category category;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

}
