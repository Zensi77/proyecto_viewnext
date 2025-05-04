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
    @ProtoField(number = 1, required = true)
    String id;

    @ProtoField(number = 2, required = true)
    String name;

    @ProtoField(number = 3, required = true)
    double price;

    @ProtoField(number = 4, required = true)
    int stock;

    @ProtoField(number = 5, required = true)
    String image;

    @ProtoField(number = 6, required = true)
    String description;

    // Datos de categoría
    @ProtoField(number = 7, required = true)
    String categoryId;

    @ProtoField(number = 8, required = true)
    String categoryName;

    // Datos de proveedor
    @ProtoField(number = 9, required = true)
    String providerId;
    @ProtoField(number = 10, required = true)
    String providerName;
    @ProtoField(number = 11, required = true)
    String providerAddress;

}
