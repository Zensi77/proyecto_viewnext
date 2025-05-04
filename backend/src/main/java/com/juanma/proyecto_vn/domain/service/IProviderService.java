package com.juanma.proyecto_vn.domain.service;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

public interface IProviderService {
    /**
     * Obtiene todos los proveedores
     * 
     * @return Lista de proveedores
     */
    List<Provider> getAllProviders();

    /**
     * Obtiene un proveedor por su ID
     * 
     * @param id ID del proveedor
     * @return Proveedor encontrado
     */
    Provider getProvider(UUID id);

    /**
     * Crea un nuevo proveedor
     * 
     * @param providerDto Datos del proveedor a crear
     * @return Proveedor creado
     */
    Provider createProvider(ProviderDto providerDto);

    /**
     * Actualiza un proveedor existente
     * 
     * @param id          ID del proveedor a actualizar
     * @param providerDto Datos actualizados del proveedor
     * @return Proveedor actualizado
     */
    Provider updateProvider(UUID id, ProviderDto providerDto);

    /**
     * Elimina un proveedor existente
     * 
     * @param id ID del proveedor a eliminar
     */
    void deleteProvider(UUID id);
}
