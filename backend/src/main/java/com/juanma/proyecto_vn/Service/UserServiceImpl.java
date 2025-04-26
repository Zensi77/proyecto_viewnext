package com.juanma.proyecto_vn.Service;

import com.juanma.proyecto_vn.Dtos.Auth.LoginDto;
import com.juanma.proyecto_vn.Dtos.Auth.UserCreateDto;
import com.juanma.proyecto_vn.Dtos.Auth.UserResponseDto;
import com.juanma.proyecto_vn.Repositorys.UserRepository;
import com.juanma.proyecto_vn.Security.JwtUtil;
import com.juanma.proyecto_vn.interfaces.IUserService;
import com.juanma.proyecto_vn.models.RoleEnum;
import com.juanma.proyecto_vn.models.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
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

        @Transactional
        public Map<String, Object> saveUser(UserCreateDto user) {
                User newUser = User.builder()
                                .email(user.getEmail())
                                .password(passwordEncoder.encode(user.getPassword()))
                                .role(RoleEnum.USER)
                                .build();

                User savedUser = userRepository.save(newUser);

                // Cargar detalles del usuario para generar token
                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String token = jwtUtil.generateToken(userDetails, savedUser.getRole().toString());

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(user.getName())
                                .email(savedUser.getEmail())
                                .role(savedUser.getRole().toString())
                                .build());
                response.put("token", token);

                return response;
        }

        @Transactional
        public Map<String, Object> login(LoginDto user) {
                // Intentar autenticar al usuario
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

                // Si llegamos aquí, la autenticación fue exitosa
                Optional<User> userFind = userRepository.findByEmail(user.getEmail());

                // Generar token JWT
                UserDetails userDetails = userDetailsService.loadUserByUsername(userFind.get().getEmail());
                String token = jwtUtil.generateToken(userDetails, userFind.get().getRole().toString());

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(userFind.get().getName())
                                .email(userFind.get().getEmail())
                                .role(userFind.get().getRole().toString())
                                .build());
                response.put("token", token);

                return response;
        }

        @Transactional
        public Map<String, Object> saveAdmin(UserCreateDto user) {
                User newUser = User.builder()
                                .email(user.getEmail())
                                .password(passwordEncoder.encode(user.getPassword()))
                                .role(RoleEnum.ADMIN)
                                .build();

                User savedUser = userRepository.save(newUser);

                // Cargar detalles del usuario para generar token
                UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
                String token = jwtUtil.generateToken(userDetails, savedUser.getRole().toString());

                // Crear respuesta con token y datos del usuario
                Map<String, Object> response = new HashMap<>();
                response.put("user", UserResponseDto.builder()
                                .name(user.getName())
                                .email(savedUser.getEmail())
                                .role(savedUser.getRole().toString())
                                .build());
                response.put("token", token);

                return response;
        }

        @Transactional
        public boolean emailExist(String email) {
                Optional<User> user = userRepository.findByEmail(email);

                return user.isPresent();
        }
}