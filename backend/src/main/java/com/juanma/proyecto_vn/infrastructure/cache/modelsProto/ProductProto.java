package com.juanma.proyecto_vn.infrastructure.cache.modelsProto;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase que representa un producto en formato protobuf para caché
 */
@Proto
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductProto {
    @ProtoField(number = 1)
    String id;

    @ProtoField(number = 2)
    String name;

    @ProtoField(number = 3, defaultValue = "0.0")
    double price;

    @ProtoField(number = 4, defaultValue = "0")
    int stock;

    @ProtoField(number = 5)
    String image;

    @ProtoField(number = 6)
    String description;

    // Datos de categoría
    @ProtoField(number = 7)
    String categoryId;

    @ProtoField(number = 8)
    String categoryName;

    // Datos de proveedor
    @ProtoField(number = 9)
    String providerId;
    @ProtoField(number = 10)
    String providerName;
    @ProtoField(number = 11)
    String providerAddress;

}
