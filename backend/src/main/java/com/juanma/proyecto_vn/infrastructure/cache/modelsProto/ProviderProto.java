package com.juanma.proyecto_vn.infrastructure.cache.modelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import lombok.Builder;

@Builder
@Proto
public class ProviderProto {
    @ProtoFactory
    public ProviderProto(UUID id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @ProtoField(number = 1)
    public UUID id;

    @ProtoField(number = 2)
    public String name;

    @ProtoField(number = 3)
    public String address;

    public static ProviderProto fromDto(ProviderDto providerDto) {
        return ProviderProto.builder()
                .id(providerDto.getId())
                .name(providerDto.getName())
                .address(providerDto.getAddress())
                .build();
    }

    public static ProviderDto toDto(ProviderProto providerProto) {
        return ProviderDto.builder()
                .id(providerProto.id)
                .name(providerProto.name)
                .address(providerProto.address)
                .build();
    }
}
