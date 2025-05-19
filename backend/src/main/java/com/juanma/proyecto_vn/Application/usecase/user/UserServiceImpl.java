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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                                .username(user.getUsername())
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
                                .roles(savedUser.getRoles())
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
                String token = jwtUtil.generateToken(userDetails);

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(userFind.get().getUsername())
                                .email(userFind.get().getEmail())
                                .roles(userFind.get().getRoles())
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
                                .roles(savedUser.getRoles())
                                .build());
                response.put("token", token);

                return response;
        }

        public boolean emailExist(String email) {
                Optional<User> user = userRepository.findByEmail(email);

                return user.isPresent();
        }
}