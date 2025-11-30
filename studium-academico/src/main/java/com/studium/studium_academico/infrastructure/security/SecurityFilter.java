package com.studium.studium_academico.infrastructure.security;

import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsersRepository usersRepository;

    private static final List<String> PUBLIC_ROUTES = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/activate",
            "/auth/resend",
            "/auth/forgot-password",
            "/auth/reset-password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // ⭐ PERMITE OPTIONS (CORS) E ROTAS PÚBLICAS
        if (method.equalsIgnoreCase("OPTIONS")
                || PUBLIC_ROUTES.stream().anyMatch(path::startsWith)) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = recoverToken(request);

            if (token != null) {
                String email = tokenService.validateToken(token);

                if (email != null && !email.trim().isEmpty()) {
                    Users user = usersRepository.findByEmail(email)
                            .orElse(null);

                    if (user != null) {
                        UserDetails userDetails = user;

                        var authentication = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Usuário autenticado: {}", email);
                    } else {
                        log.warn("Usuário não encontrado para email: {}", email);
                    }
                } else {
                    log.warn("Token inválido ou email nulo.");
                }
            }

        } catch (Exception e) {
            log.error("Erro no SecurityFilter: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }

        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            return token.isEmpty() ? null : token;
        }

        return null;
    }
}
