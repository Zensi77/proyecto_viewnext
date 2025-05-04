package com.juanma.proyecto_vn.infrastructure.cache;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.ProductProto;

@ProtoSchema(includeClasses = {
                ProductProto.class,
                com.juanma.proyecto_vn.infrastructure.cache.UUIDAdapter.class,
}, schemaFileName = "product.proto", schemaFilePath = "proto/")
public interface ProductProtoSchemaInitializer extends GeneratedSchema {
}
