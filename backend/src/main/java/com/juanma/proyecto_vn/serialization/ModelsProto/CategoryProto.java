package com.juanma.proyecto_vn.serialization.ModelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Proto
public class CategoryProto {
    @ProtoFactory
    public CategoryProto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @ProtoField(number = 1, required = true)
    public UUID id;

    @ProtoField(number = 2, required = true)
    public String name;

}
