package com.juanma.proyecto_vn.infrastructure.filter;

import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

    @Component
    public class AccountSuspendFilter extends OncePerRequestFilter {
        @Autowired
        private UserRepository userRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest req,
                                        HttpServletResponse res,
                                        FilterChain chain) throws ServletException, IOException {

            UUID userId = (UUID) req.getAttribute("userId");

            if (userId != null) {
                Optional<User> user = userRepository.findById(userId);
                if (user.isPresent()) {
                    if (!user.get().isEnabled()) {
                        throw new DisabledException("Account is suspended");
                    }
                } else if (user.get().isAccountNonLocked()) {
                    throw new DisabledException("Account is blocked");
                }
            }
                chain.doFilter(req, res);

        }
    }

