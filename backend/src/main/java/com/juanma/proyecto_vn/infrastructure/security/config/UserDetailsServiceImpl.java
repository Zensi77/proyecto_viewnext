package com.juanma.proyecto_vn.infrastructure.security.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.UserEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaUserRepository;

/**
 * Implementación de UserDetailsService para cargar los detalles del usuario
 * desde la base de datos.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JpaUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + username);
        }

        return User.builder()
                .username(user.get().getEmail())
                .password(user.get().getPassword())
                .authorities(
                        user.get().getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getAuthority().name()))
                                .toList()
                )
                .disabled(!user.get().isEnabled())
                .accountLocked(!user.get().isAccountNonLocked())
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}