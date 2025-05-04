package com.juanma.proyecto_vn.Application.usecase.provider;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Application.validator.ProviderValidator;
import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.domain.repository.ProviderRepository;
import com.juanma.proyecto_vn.domain.service.IProviderService;

import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de los casos de uso relacionados con proveedores
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProviderServiceImpl implements IProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderValidator providerValidator;

    @Override
    public List<Provider> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providers;
    }

    @Override
    public Provider getProvider(UUID id) {
        Provider provider = providerRepository.findById(id);
        return provider;
    }

    @Override
    public Provider createProvider(ProviderDto providerDto) {
        providerValidator.validateProviderExists(providerDto.getName());

        Provider provider = Provider.builder()
                .name(providerDto.getName())
                .address(providerDto.getAddress())
                .build();
        providerRepository.save(provider);
        return provider;
    }

    @Override
    public Provider updateProvider(UUID id, ProviderDto providerDto) {
        Provider provider = providerRepository.findById(id);

        provider.setName(providerDto.getName());
        provider.setAddress(providerDto.getAddress());

        providerRepository.save(provider);

        return provider;
    }

    @Override
    public void deleteProvider(UUID id) {
        Provider provider = providerRepository.findById(id);

        providerRepository.delete(provider);
    }

}
