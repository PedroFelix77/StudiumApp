package com.studium.studium_academico.business.service;

import com.studium.studium_academico.infrastructure.entity.PasswordResetToken;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.PasswordResetTokenRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsersRepository usersRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${frontend.url}")
    private String frontendUrl;

    // 1. Gera token e envia por e-mail (ou retorna para o controller)
    @Transactional
    public PasswordResetToken generateResetToken(String email) {

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        tokenRepository.findByUser(user)
                .ifPresent(tokenRepository::delete);

        String tokenString = UUID.randomUUID().toString();

        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenString)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        PasswordResetToken saved = tokenRepository.save(token);

        String resetLink = frontendUrl + "/reset-password?token=" + tokenString;

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink,
                user.getName()
        );

        log.info("Token de reset gerado para {}: {}", email, tokenString);

        return saved;
    }


    // 2. Validação do token
    public PasswordResetToken validateToken(String token) {

        PasswordResetToken reset = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (reset.isUsed()) {
            throw new IllegalStateException("Token já foi utilizado");
        }

        if (reset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expirado");
        }

        return reset;
    }


    // 3. Resetar a senha
    @Transactional
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken reset = validateToken(token);
        Users user = reset.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        reset.setUsed(true);
        tokenRepository.save(reset);

        log.info("Senha redefinida com sucesso para o usuário {}", user.getEmail());
    }

    public void resendResetToken(String oldToken) {
        PasswordResetToken old = tokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        Users user = old.getUser();

        tokenRepository.delete(old);

        String newTokenString = UUID.randomUUID().toString();

        PasswordResetToken newToken = PasswordResetToken.builder()
                .token(newTokenString)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        tokenRepository.save(newToken);

        String resetLink = frontendUrl + "/reset-password?token=" + newTokenString;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink, user.getName());

        log.info("Token de reset gerado para: {}", user.getEmail());
    }
}
