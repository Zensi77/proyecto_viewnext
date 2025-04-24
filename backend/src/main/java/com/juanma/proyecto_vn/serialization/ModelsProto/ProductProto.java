package com.juanma.proyecto_vn.serialization.ModelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

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

}
