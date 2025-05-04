package com.juanma.proyecto_vn.interfaces.rest.mapper;

import org.springframework.stereotype.Component;

import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

@Component
public class ProviderDtoMapper {
    public ProviderDto toDto(Provider provider) {
        if (provider == null) {
            return null;
        }

        return ProviderDto.builder()
                .id(provider.getId())
                .name(provider.getName())
                .address(provider.getAddress())
                .build();
    }

    public Provider toDomain(ProviderDto providerDto) {
        if (providerDto == null) {
            return null;
        }

        return Provider.builder()
                .id(providerDto.getId())
                .name(providerDto.getName())
                .address(providerDto.getAddress())
                .build();
    }
}
