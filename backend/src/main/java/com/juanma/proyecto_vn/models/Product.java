package com.juanma.proyecto_vn.models;

import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(exclude = { "provider", "category" }) // Excluye las relaciones para evitar problemas de serialización
@EqualsAndHashCode(callSuper = true, exclude = { "provider", "category" })
@Builder
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
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

    @Column(name = "stock", nullable = false, columnDefinition = "int default 0")
    private int stock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_provider", referencedColumnName = "id")
    private Provider provider;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_category", referencedColumnName = "id")
    private Category category;
}