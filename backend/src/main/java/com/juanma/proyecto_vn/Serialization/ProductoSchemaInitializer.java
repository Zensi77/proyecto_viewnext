package com.juanma.proyecto_vn.Serialization;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

import com.juanma.proyecto_vn.Serialization.ModelsProto.CategoryProto;
import com.juanma.proyecto_vn.Serialization.ModelsProto.ProductProto;
import com.juanma.proyecto_vn.Serialization.ModelsProto.ProviderProto;

@ProtoSchema(includeClasses = {
        ProductProto.class,
        CategoryProto.class,
        ProviderProto.class,
        com.juanma.proyecto_vn.Serialization.UUIDAdapter.class,
}, schemaFileName = "product.proto", schemaFilePath = "proto/")
public interface ProductoSchemaInitializer extends GeneratedSchema {
}
