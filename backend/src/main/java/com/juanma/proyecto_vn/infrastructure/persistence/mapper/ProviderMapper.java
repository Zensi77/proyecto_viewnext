package com.juanma.proyecto_vn.infrastructure.persistence.mapper;

import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProviderEntity;

import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades JPA y modelos de dominio para
 * proveedores
 */
@Component
public class ProviderMapper {

    /**
     * Convierte de entidad JPA a modelo de dominio
     */
    public Provider toDomain(ProviderEntity entity) {
        if (entity == null) {
            return null;
        }

        return Provider.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .build();
    }

    /**
     * Convierte de modelo de dominio a entidad JPA
     */
    public ProviderEntity toEntity(Provider domain) {
        if (domain == null) {
            return null;
        }

        return ProviderEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .address(domain.getAddress())
                .build();
    }
}