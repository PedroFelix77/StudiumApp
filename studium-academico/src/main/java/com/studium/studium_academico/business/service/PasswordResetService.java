package com.studium.studium_academico.business.service;

import com.studium.studium_academico.infrastructure.entity.PasswordResetToken;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.PasswordResetTokenRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PasswordResetService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Solicitação de reset (gera e envia o token)
    @Transactional
    public PasswordResetToken generateResetToken(String email) {

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String tokenString = UUID.randomUUID().toString();

        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        PasswordResetToken saved = tokenRepository.save(token);

        log.info("Token de reset gerado para {}: {}", email, tokenString);

        return saved;
    }

    // 2. Validação do token
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken reset = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (reset.isUsed()) {
            throw new RuntimeException("Token já utilizado");
        }

        if (reset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        return reset;
    }

    // 3. Redefinir senha
    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken reset = validateToken(token);

        Users user = reset.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        reset.setUsed(true);
        tokenRepository.save(reset);

        log.info("Senha redefinida para usuário {}", user.getEmail());
    }
}
