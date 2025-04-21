package com.juanma.proyecto_vn.interfaces;

import java.util.List;

import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;

public interface IProviderService {
    List<ProviderDto> getAllProviders();

    ProviderDto getProvider(String id);

    ProviderDto createProvider(ProviderDto providerDto);

    ProviderDto updateProvider(String id, ProviderDto providerDto);

    void deleteProvider(String id);
}
