package com.juanma.proyecto_vn.models;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Builder
@Proto
public class Provider extends BaseEntity {

    @ProtoField(number = 1)
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    protected UUID id;

    @ProtoField(number = 2)
    @Column(name = "name", nullable = false, unique = true)
    protected String name;

    @ProtoField(number = 3)
    @Column(name = "address", nullable = false)
    protected String address;

    // Constructor para ProtoStream
    @ProtoFactory
    public Provider(UUID id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

}
