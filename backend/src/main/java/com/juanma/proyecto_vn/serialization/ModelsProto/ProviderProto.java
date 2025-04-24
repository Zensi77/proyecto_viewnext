package com.juanma.proyecto_vn.serialization.ModelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@Proto
public class ProviderProto {
    @ProtoFactory
    public ProviderProto(UUID id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @ProtoField(number = 1, required = true)
    public UUID id;

    @ProtoField(number = 2, required = true)
    public String name;

    @ProtoField(number = 3, required = true)
    public String address;
}
