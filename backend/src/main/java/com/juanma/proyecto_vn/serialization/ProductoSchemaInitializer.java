package com.juanma.proyecto_vn.serialization;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

import com.juanma.proyecto_vn.serialization.ModelsProto.CategoryProto;
import com.juanma.proyecto_vn.serialization.ModelsProto.ProductProto;
import com.juanma.proyecto_vn.serialization.ModelsProto.ProviderProto;

@ProtoSchema(includeClasses = {
                ProductProto.class,
                CategoryProto.class,
                ProviderProto.class,
                com.juanma.proyecto_vn.serialization.UUIDAdapter.class,
}, schemaFileName = "product.proto", schemaFilePath = "proto/")
public interface ProductoSchemaInitializer extends GeneratedSchema {
}
