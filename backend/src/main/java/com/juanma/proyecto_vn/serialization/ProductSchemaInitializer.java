package com.juanma.proyecto_vn.serialization;

import org.infinispan.protostream.annotations.ProtoSchema;

import com.juanma.proyecto_vn.models.Category;
import com.juanma.proyecto_vn.models.Product;
import com.juanma.proyecto_vn.models.Provider;

import org.infinispan.protostream.GeneratedSchema;

@ProtoSchema(includeClasses = {
                Product.class,
                Category.class,
                Provider.class,
                UUIDAdapter.class
}, schemaFileName = "product.proto", schemaFilePath = "proto/")
public interface ProductSchemaInitializer extends GeneratedSchema {
}