package com.juanma.proyecto_vn.Application.usecase.user;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.domain.service.IUserService;
import com.juanma.proyecto_vn.infrastructure.security.jwt.JwtUtil;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserCreateDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;
import com.juanma.proyecto_vn.shared.Utils.enums.RoleEnum;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.util.*;

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

        public List<User> getAll() {
                return userRepository.findAll();
        }

        public Map<String, Object> saveUser(UserCreateDto user) {
                User newUser = User.builder()
                                .email(user.getEmail())
                                .password(passwordEncoder.encode(user.getPassword()))
                                .roles(new HashSet<>())
                                .username(user.getUsername())
                                .enabled(true)
                                .accountNonLocked(true)
                                .build();

                User savedUser = userRepository.save(newUser,false);

                // Cargar detalles del usuario para generar token
                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(user.getUsername())
                                .email(savedUser.getEmail())
                                .name(savedUser.getUsername())
                                .roles(savedUser.getRoles().stream()
                                                .map(role -> role.getName().name())
                                                .toList())
                                .enabled(savedUser.isEnabled())
                                .accountNonLocked(savedUser.isAccountNonLocked())
                                .build());
                response.put("token", token);

                return response;
        }

        public Map<String, Object> login(LoginDto user) {
                // Intentar autenticar al usuario
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

                // Si llegamos aquí, la autenticación fue exitosa
                Optional<User> userFind = userRepository.findByEmail(user.getEmail());

                // Generar token JWT
                UserDetails userDetails = userDetailsService.loadUserByUsername(userFind.get().getEmail());

                if (!userDetails.isEnabled()){
                        throw new DisabledException("Usuario deshabilitado");
                } else if (!userDetails.isAccountNonLocked()) {
                        throw new LockedException("Usuario bloqueado");
                }

                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(userFind.get().getUsername())
                                .email(userFind.get().getEmail())
                                .roles(userFind.get().getRoles().stream()
                                                .map(role -> role.getName().name())
                                                .toList())
                                .enabled(userFind.get().isEnabled())
                                .accountNonLocked(userFind.get().isAccountNonLocked())
                                .build());
                response.put("token", token);

                return response;
        }

        public Map<String, Object> saveAdmin(UserCreateDto user) {
                User newUser = User.builder()
                                .email(user.getEmail())
                                .username(user.getUsername())
                                .password(passwordEncoder.encode(user.getPassword()))
                                .build();

                User savedUser = userRepository.save(newUser, true);

                // Cargar detalles del usuario para generar token
                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(user.getUsername())
                                .email(savedUser.getEmail())
                                .roles(savedUser.getRoles().stream()
                                                .map(role -> role.getName().name())
                                                .toList())
                                .build());
                response.put("token", token);

                return response;
        }

        public boolean emailExist(String email) {
                Optional<User> user = userRepository.findByEmail(email);

                return user.isPresent();
        }
}