package com.juanma.proyecto_vn.models;

import lombok.NoArgsConstructor;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Table(name = "product")
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = { "provider", "category" })
@Builder
@Proto
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    @ProtoField(number = 1)
    protected UUID id;

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
    @ProtoField(number = 2)
    protected String name;

    @Column(name = "price", nullable = false, columnDefinition = "decimal(10,2)")
    @ProtoField(number = 3, required = true)
    @Builder.Default
    protected double price = 0.0;

    @Column(name = "image", nullable = false)
    @ProtoField(number = 4)
    protected String image;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    @ProtoField(number = 5)
    protected String description;

    @Column(name = "stock", nullable = false, columnDefinition = "int default 0")
    @ProtoField(number = 6, required = true)
    protected int stock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_provider", referencedColumnName = "id")
    @ProtoField(number = 7)
    protected Provider provider;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_category", referencedColumnName = "id")
    @ProtoField(number = 8)
    protected Category category;

}