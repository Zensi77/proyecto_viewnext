package com.juanma.proyecto_vn.infrastructure.cache.mapper;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.ProductProto;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapeador para convertir entre el modelo de dominio Product y el objeto
 * protobuf ProductProto para caché
 */
@Component
public class ProductCacheMapper {

    /**
     * Convierte un objeto de dominio a un objeto protobuf para caché
     */
    public ProductProto toProtobuf(Product product) {
        if (product == null) {
            return null;
        }

        ProductProto builder = ProductProto.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .image(product.getImage())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId().toString() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .providerId(product.getProvider() != null ? product.getProvider().getId().toString() : null)
                .providerName(product.getProvider() != null ? product.getProvider().getName() : null)
                .providerAddress(product.getProvider() != null && product.getProvider().getAddress() != null
                        ? product.getProvider().getAddress()
                        : null)
                .build();

        return builder;
    }

    /**
     * Convierte un objeto protobuf a un objeto de dominio
     */
    public Product toDomain(ProductProto proto) {
        if (proto == null) {
            return null;
        }

        Category category = Category.builder()
                .id(UUID.fromString(proto.getCategoryId()))
                .name(proto.getCategoryName())
                .build();

        Provider provider = Provider.builder()
                .id(UUID.fromString(proto.getProviderId()))
                .name(proto.getProviderName())
                .address(proto.getProviderAddress() == null ? null : proto.getProviderAddress())
                .build();

        return Product.builder()
                .id(UUID.fromString(proto.getId()))
                .name(proto.getName())
                .price(proto.getPrice())
                .image(proto.getImage())
                .stock(proto.getStock())
                .description(proto.getDescription())
                .category(category)
                .provider(provider)
                .build();
    }
}