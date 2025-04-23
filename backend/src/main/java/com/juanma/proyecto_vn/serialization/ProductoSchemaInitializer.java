// src/main/java/com/tuapp/serialization/ProductoSchemaInitializer.java
package com.juanma.proyecto_vn.serialization;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.ProtoSchema;

import com.juanma.proyecto_vn.models.Category;
import com.juanma.proyecto_vn.models.Product;
import com.juanma.proyecto_vn.models.Provider;

@ProtoSchema(includeClasses = {
        Product.class,
        Category.class,
        Provider.class,
        com.juanma.proyecto_vn.serialization.UUIDAdapter.class,
}, schemaFileName = "product.proto", schemaFilePath = "proto/")
public interface ProductoSchemaInitializer extends GeneratedSchema {
}
