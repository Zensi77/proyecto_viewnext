package com.juanma.proyecto_vn.infrastructure.persistence.repository;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleRepository extends JpaRepository<Role, Long> {
    Role findByAuthority(RoleType name);
}

