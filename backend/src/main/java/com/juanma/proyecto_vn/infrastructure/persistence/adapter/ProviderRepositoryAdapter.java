package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.domain.repository.ProviderRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProviderEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.ProviderMapper;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaProviderRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto de repositorio de proveedores conectando
 * con JPA
 */
@Component
@RequiredArgsConstructor
public class ProviderRepositoryAdapter implements ProviderRepository {

    private final JpaProviderRepository jpaProviderRepository;
    private final ProviderMapper providerMapper;

    @Override
    public List<Provider> findAll() {
        return jpaProviderRepository.findAll().stream()
                .map(providerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Provider> findByNameContaining(String name) {
        return jpaProviderRepository.findByNameContaining(name).stream()
                .map(providerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Provider findById(UUID id) {
        ProviderEntity prov = jpaProviderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
         
        return providerMapper.toDomain(prov);
    }

    @Override
    public Provider save(Provider provider) {
        return providerMapper.toDomain(jpaProviderRepository.save(providerMapper.toEntity(provider)));
    }

    @Override
    public void delete(Provider provider) {
        jpaProviderRepository.delete(providerMapper.toEntity(provider));
    }

    @Override
    public void deleteById(UUID id) {
        jpaProviderRepository.deleteById(id);
    }

}