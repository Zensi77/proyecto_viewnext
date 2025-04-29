package com.juanma.proyecto_vn.infrastructure.cache;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.CategoryProto;
import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.ProductProto;
import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.ProviderProto;



@ProtoSchema(includeClasses = {
                ProductProto.class,
                CategoryProto.class,
                ProviderProto.class,
                com.juanma.proyecto_vn.infrastructure.cache.UUIDAdapter.class,
}, schemaFileName = "product.proto", schemaFilePath = "proto/")
public interface ProductoSchemaInitializer extends GeneratedSchema {
}
