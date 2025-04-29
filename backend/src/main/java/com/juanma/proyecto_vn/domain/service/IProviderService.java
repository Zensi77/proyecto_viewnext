package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

public interface IProviderService {
    List<ProviderDto> getAllProviders();

    ProviderDto getProvider(UUID id);

    ProviderDto createProvider(ProviderDto providerDto);

    ProviderDto updateProvider(UUID id, ProviderDto providerDto);

    void deleteProvider(UUID id);
}
