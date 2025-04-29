package com.juanma.proyecto_vn.infrastructure.cache;

import java.util.UUID;

import org.infinispan.protostream.annotations.ProtoAdapter;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

@ProtoAdapter(UUID.class)
public class UUIDAdapter {
    @ProtoFactory
    public UUID create(String value) {
        return UUID.fromString(value);
    }

    @ProtoField(number = 1)
    public String value(UUID uuid) {
        return uuid.toString();
    }
}