package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;
import com.juanma.proyecto_vn.Exception.ResourceNotFoundException;
import com.juanma.proyecto_vn.Repositorys.ProviderRepository;
import com.juanma.proyecto_vn.interfaces.IProviderService;
import com.juanma.proyecto_vn.models.Provider;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProviderServiceImpl implements IProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Override
    public List<ProviderDto> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        return providers.stream().map(this::mapToDto).toList();
    }

    @Override
    public ProviderDto getProvider(UUID id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        return mapToDto(provider);
    }

    @Override
    public ProviderDto createProvider(ProviderDto providerDto) {
        Provider provider = Provider.builder()
                .name(providerDto.getName())
                .address(providerDto.getAddress())
                .build();
        providerRepository.save(provider);
        return mapToDto(provider);
    }

    @Override
    public ProviderDto updateProvider(UUID id, ProviderDto providerDto) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        provider.setName(providerDto.getName());
        provider.setAddress(providerDto.getAddress());

        providerRepository.save(provider);

        return mapToDto(provider);
    }

    @Override
    public void deleteProvider(UUID id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        providerRepository.delete(provider);
    }

    private ProviderDto mapToDto(Provider provider) {
        return ProviderDto.builder()
                .id(provider.getId())
                .name(provider.getName())
                .address(provider.getAddress())
                .build();
    }

}
