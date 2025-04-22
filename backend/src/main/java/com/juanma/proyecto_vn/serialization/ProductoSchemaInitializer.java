package com.juanma.proyecto_vn.serialization;

import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;
import com.juanma.proyecto_vn.models.Product;

import org.infinispan.protostream.SerializationContextInitializer;

@AutoProtoSchemaBuilder(includeClasses = {
                Product.class
}, schemaFileName = "product.proto", schemaFilePath = "proto/", service = true)
public interface ProductoSchemaInitializer extends SerializationContextInitializer {
}
