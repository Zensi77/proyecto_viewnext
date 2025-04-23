package com.juanma.proyecto_vn.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Entity
@Setter
@Data
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@Builder
@Table(name = "category")
@NoArgsConstructor
@Proto
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    @ProtoField(number = 1)
    protected UUID id;

    @Column(name = "name", nullable = false, unique = true)
    @ProtoField(number = 2)
    protected String name;

    @ProtoFactory
    public Category(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
