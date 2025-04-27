package com.juanma.proyecto_vn.Serialization.ModelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;

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

    @ProtoField(number = 1, required = true)
    public UUID id;

    @ProtoField(number = 2, required = true)
    public String name;

    @ProtoField(number = 3, required = true)
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
