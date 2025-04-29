package com.juanma.proyecto_vn.infrastructure.cache.modelsProto;

import java.util.UUID;

import org.infinispan.protostream.annotations.Proto;
import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryDto;

import lombok.Builder;

@Builder
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

    public static CategoryProto fromDto(CategoryDto categoryDto) {
        return CategoryProto.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toDto(CategoryProto categoryProto) {
        return CategoryDto.builder()
                .id(categoryProto.id)
                .name(categoryProto.name)
                .build();
    }
}
