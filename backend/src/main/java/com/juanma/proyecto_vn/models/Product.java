package com.juanma.proyecto_vn.models;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "product")
@NoArgsConstructor
@Setter
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

    @ProtoFactory
    Product(UUID id, String name, double price, String image, String description, int stock, Provider provider,
            Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.stock = stock;
        this.provider = provider;
        this.category = category;
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(10,2)")
    @Builder.Default
    private double price = 0.0;

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

    @ProtoField(number = 1)
    public UUID getId() {
        return id;
    }

    @ProtoField(number = 2)
    public String getName() {
        return name;
    }

    @ProtoField(number = 3, required = true)
    public double getPrice() {
        return price;
    }

    @ProtoField(number = 4)
    public String getImage() {
        return image;
    }

    @ProtoField(number = 5)
    public String getDescription() {
        return description;
    }

    @ProtoField(number = 6, required = true)
    public int getStock() {
        return stock;
    }

    @ProtoField(number = 7)
    public Provider getProvider() {
        return provider;
    }

    @ProtoField(number = 8)
    public Category getCategory() {
        return category;
    }
}