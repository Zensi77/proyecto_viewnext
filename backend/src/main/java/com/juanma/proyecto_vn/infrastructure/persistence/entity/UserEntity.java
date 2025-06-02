package com.juanma.proyecto_vn.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Builder
@Data
@EqualsAndHashCode(callSuper = true) // Para que no de error al hacer equals y hashCode
@AllArgsConstructor
@Table(name = "user")
@NoArgsConstructor
public class UserEntity extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default true")
    private boolean enabled = true;

    @Column(name = "account_non_locked", nullable = false, columnDefinition = "boolean default true")
    private boolean accountNonLocked = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartEntity cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<ProductEntity> wishlists = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getAuthority().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
