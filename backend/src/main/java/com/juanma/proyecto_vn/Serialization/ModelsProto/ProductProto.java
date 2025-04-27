package com.juanma.proyecto_vn.Serialization.ModelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;

import lombok.Builder;

@Builder
@Proto
public class ProductProto {
    @ProtoFactory
    ProductProto(UUID id, String name, double price, String image, String description, int stock,
            ProviderProto provider,
            CategoryProto category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
        this.description = description;
        this.stock = stock;
        this.provider = provider;
        this.category = category;
    }

    @ProtoField(number = 1, required = true)
    public UUID id;

    @ProtoField(number = 2, required = true)
    public String name;

    @ProtoField(number = 3, required = true)
    public double price;

    @ProtoField(number = 4, required = true)
    public String image;

    @ProtoField(number = 5, required = true)

    public String description;

    @ProtoField(number = 6, required = true)
    public int stock;

    @ProtoField(number = 7, required = true)
    public ProviderProto provider;

    @ProtoField(number = 8, required = true)
    public CategoryProto category;

    public static ProductProto fromDto(GetProductDto productDto) {
        return ProductProto.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .image(productDto.getImage())
                .description(productDto.getDescription())
                .stock(productDto.getStock())
                .provider(ProviderProto.fromDto(productDto.getProvider()))
                .category(CategoryProto.fromDto(productDto.getCategory()))
                .build();
    }

    public static GetProductDto toDto(ProductProto productProto) {
        return GetProductDto.builder()
                .id(productProto.id)
                .name(productProto.name)
                .price(productProto.price)
                .image(productProto.image)
                .description(productProto.description)
                .stock(productProto.stock)
                .provider(ProviderProto.toDto(productProto.provider))
                .category(CategoryProto.toDto(productProto.category))
                .build();
    }
}
