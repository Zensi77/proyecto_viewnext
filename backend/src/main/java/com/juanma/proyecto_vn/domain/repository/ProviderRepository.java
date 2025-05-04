package com.juanma.proyecto_vn.domain.repository;

import com.juanma.proyecto_vn.domain.model.Provider;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de salida (secundario) para la persistencia de proveedores
 */
public interface ProviderRepository {
    List<Provider> findAll();

    Provider findById(UUID id);

    List<Provider> findByNameContaining(String name);

    Provider save(Provider provider);

    void delete(Provider provider);

    void deleteById(UUID id);

}