package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import com.juanma.proyecto_vn.shared.Utils.enums.RoleEnum;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@AllArgsConstructor
@Table(name = "user")
@NoArgsConstructor
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String fullName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, updatable = false, columnDefinition = "enum('USER', 'ADMIN')")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoleEnum role = RoleEnum.USER;

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default false")
    private boolean enabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartEntity cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders;
}
