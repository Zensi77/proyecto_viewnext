package com.juanma.proyecto_vn.Repositorys;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.juanma.proyecto_vn.models.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    Optional<Provider> findByName(String name);

    @Query(value = "SELECT * FROM providers WHERE id = :id", nativeQuery = true)
    Optional<Provider> findByIdNative(String id);
}
