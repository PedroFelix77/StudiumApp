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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;
    @Autowired
    UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var token = this.recoverToken(request);

            if(token != null && !token.trim().isEmpty()){
                var email = tokenService.validateToken(token);

                // VERIFICAÇÃO CRÍTICA: Só prossegue se email não for nulo/vazio
                if(email != null && !email.trim().isEmpty()){
                    var userOpt = usersRepository.findByEmail(email);

                    if(userOpt.isPresent()) {
                        Users user = userOpt.get();
                        UserDetails userDetails = user;

                        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("Usuário autenticado: {}", email);
                    } else {
                        log.warn("Usuário não encontrado para email: {}", email);
                    }
                } else {
                    log.warn("Token válido mas email vazio ou nulo");
                }
            }
        } catch (Exception e) {
            log.error("Erro no SecurityFilter: {}", e.getMessage());
            // Não quebra a requisição - apenas continua
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }

        // VERIFICAÇÃO MELHORADA: Confirma que tem "Bearer " e conteúdo depois
        if (authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "").trim();
            return token.isEmpty() ? null : token;
        }

        return null;
    }
}
