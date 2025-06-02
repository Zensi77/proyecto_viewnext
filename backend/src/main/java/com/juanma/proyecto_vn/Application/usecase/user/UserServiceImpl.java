package com.juanma.proyecto_vn.Application.usecase.user;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.ProductRepository;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.domain.service.IUserService;
import com.juanma.proyecto_vn.infrastructure.security.jwt.JwtUtil;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.UserDtoMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación de los casos de uso relacionados con usuarios
 */
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements IUserService {
        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserDtoMapper userDtoMapper;

        @Autowired
        private ProductRepository productRepository;

        public List<UserResponseDto> getAll() {
                List<User> users = userRepository.findAll();

                return users.stream()
                                .map(userDtoMapper::toDto)
                                .toList();
        }

        public Map<String, Object> saveUser(User user) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userRepository.save(user, false);

                // Cargar detalles del usuario para generar token
                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", userDtoMapper.toDto(savedUser));
                response.put("token", token);

                return response;
        }

        public Map<String, Object> login(LoginDto user) {
                // Intentar autenticar al usuario
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

                // Si llegamos aquí, la autenticación fue exitosa
                Optional<User> userFind = userRepository.findByEmail(user.getEmail());

                UserDetails userDetails = userDetailsService.loadUserByUsername(userFind.get().getEmail());

                if (!userDetails.isEnabled()) {
                        throw new DisabledException("Usuario deshabilitado");
                } else if (!userDetails.isAccountNonLocked()) {
                        throw new LockedException("Usuario bloqueado");
                }

                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", userDtoMapper.toDto(userFind.get()));
                response.put("token", token);

                return response;
        }

        public Map<String, Object> saveAdmin(User user) {

                User savedUser = userRepository.save(user, true);

                // Cargar detalles del usuario para generar token
                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", userDtoMapper.toDto(savedUser));
                response.put("token", token);

                return response;
        }

        public Map<String, Object> updateUser(User user, UUID userId) {
                Optional<User> userFind = userRepository.findById(userId);

                if (userFind.isPresent()) {
                        User userDb = User.builder()
                                        .id(user.getId())
                                        .email(user.getEmail())
                                        .username(user.getUsername())
                                        .wishlists(user.getWishlists() != null ? user.getWishlists()
                                                        : userFind.get().getWishlists())
                                        .roles(user.getRoles())
                                        .enabled(user.isEnabled())
                                        .accountNonLocked(user.isAccountNonLocked())
                                        .build();

                        if (user.getPassword() != null) {
                                userDb.setPassword(passwordEncoder.encode(user.getPassword()));
                        } else {
                                userDb.setPassword(userFind.get().getPassword());
                        }

                        User savedUser = userRepository.modify(userDb);

                        Map<String, Object> response = new HashMap<>();
                        response.put("user", userDtoMapper.toDto(savedUser));
                        return response;
                } else {
                        throw new IllegalArgumentException("Usuario no encontrado");
                }
        }

        public boolean emailExist(String email) {
                Optional<User> user = userRepository.findByEmail(email);

                if (user == null) {
                        return false;
                }

                return user.isPresent();
        }

        @Override
        public List<Product> getUserWishlist(UUID userId) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) {
                        throw new ResourceNotFoundException("Usuario no encontrado");
                }
                User user = userOpt.get();
                if (user.getWishlists() == null) {
                        user.setWishlists(new ArrayList<>());
                        userRepository.modify(user);
                }
                return user.getWishlists();
        }

        @Override
        public boolean addToWishlist(UUID userId, UUID productId) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) {
                        throw new ResourceNotFoundException("Usuario no encontrado");
                }

                Product product = productRepository.findById(productId);
                if (product == null) {
                        throw new ResourceNotFoundException("Producto no encontrado con ID: " + productId);
                }

                User user = userOpt.get();

                // Inicializar la lista de deseos si es nula
                if (user.getWishlists() == null || !(user.getWishlists() instanceof ArrayList)) {
                        user.setWishlists(new ArrayList<>(user.getWishlists() != null ? user.getWishlists() : Collections.emptyList()));
                }

                // Verificar si el producto ya está en la wishlist
                boolean alreadyInWishlist = user.getWishlists().stream()
                                .anyMatch(p -> p.getId().equals(productId));

                if (alreadyInWishlist) {
                        return false; // El producto ya estaba en la wishlist
                }

                // Añadir el producto a la wishlist
                user.getWishlists().add(product);
                userRepository.modify(user);
                return true;
        }

        @Override
        // @CacheEvict(cacheNames = { "userWishlistCache", "userWishlistIdsCache" }, key
        // = "#userId")
        public boolean removeFromWishlist(UUID userId, UUID productId) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) {
                        throw new ResourceNotFoundException("Usuario no encontrado");
                }
                User user = userOpt.get();

                // Si la wishlist es nula, no hay nada que eliminar
                if (user.getWishlists() == null) {
                        return false;
                }

                // Eliminar el producto de la wishlist
                user.setWishlists(new ArrayList<>(user.getWishlists())); // Asegurarse de que sea una lista mutable
                boolean removed = user.getWishlists().removeIf(p -> p.getId().equals(productId));

                if (removed) {
                        userRepository.modify(user);
                        return true;
                } else {
                        return false; // El producto no estaba en la wishlist
                }
        }

        /**
         * Obtiene solo los IDs de los productos en la wishlist del usuario
         * Método optimizado para verificaciones de favoritos
         * 
         * @param userId ID del usuario
         * @return Set con los IDs de los productos favoritos
         */
        @Override
        // @Cacheable(value = "userWishlistIdsCache", key = "#userId")
        public Set<UUID> getUserWishlistIds(UUID userId) {
                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isEmpty()) {
                        throw new ResourceNotFoundException("Usuario no encontrado");
                }
                User user = userOpt.get();
                if (user.getWishlists() == null) {
                        return new HashSet<>();
                }

                // Extrae solo los IDs de los productos favoritos
                return user.getWishlists().stream()
                                .map(Product::getId)
                                .collect(Collectors.toSet());
        }
}