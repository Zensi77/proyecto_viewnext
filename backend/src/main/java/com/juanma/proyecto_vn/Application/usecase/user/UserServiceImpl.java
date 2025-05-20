package com.juanma.proyecto_vn.Application.usecase.user;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.domain.service.IUserService;
import com.juanma.proyecto_vn.infrastructure.security.jwt.JwtUtil;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.LoginDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.auth.UserResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.UserDtoMapper;

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

        @Autowired
        private UserDtoMapper userDtoMapper;

        public List<UserResponseDto> getAll() {
                List<User> users = userRepository.findAll();

                return users.stream()
                                .map(userDtoMapper::toDto)
                                .toList();
        }

        public Map<String, Object> saveUser(User user) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userRepository.save(user,false);

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

                if (!userDetails.isEnabled()){
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

        public Map<String, Object> updateUser(User user, UUID userId){
                Optional<User> userFind = userRepository.findById(userId);

                if (userFind.isPresent()){
                        User userDb = User.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .username(user.getUsername())
                                .roles(user.getRoles())
                                .enabled(user.isEnabled())
                                .accountNonLocked(user.isAccountNonLocked())
                                .build();

                        if (user.getPassword() != null){
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

                return user.isPresent();
        }
}