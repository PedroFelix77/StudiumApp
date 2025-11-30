package com.studium.studium_academico.business.service;

import com.studium.studium_academico.infrastructure.entity.ActivationToken;
import com.studium.studium_academico.infrastructure.entity.EntityStatus;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.exceptions.TokenExpiredException;
import com.studium.studium_academico.infrastructure.repository.ActivationTokenRepository;
import com.studium.studium_academico.infrastructure.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivationService {

    @Autowired
    private ActivationTokenRepository tokenRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Value("${frontend.url}")
    private String frontendUrl;

    @Transactional
    public void generateAndSendActivationLink(Users user) {
        tokenRepository.deleteByUserAndUsedFalse(user);

        ActivationToken token = ActivationToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .email(user.getEmail())
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        ActivationToken savedToken = tokenRepository.save(token);

        String activationLink = frontendUrl + "/activate?token=" + savedToken.getToken();
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            emailService.sendActivationEmail(
                                    user.getEmail(),
                                    activationLink,
                                    user.getName()
                            );
                            log.info("Email de ativação enviado com sucesso para: {}", user.getEmail());
                        } catch (Exception ex) {
                            log.error("Falha ao enviar email de ativação para: {}", user.getEmail());
                            tokenRepository.delete(savedToken);
                        }
                    }
                }
        );


        log.info("Link de ativação criado para {}", user.getEmail());
    }

    @Transactional
    public void activateAccount(String token, String newPassword) {

        ActivationToken activationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (activationToken.isUsed()) {
            throw new RuntimeException("Token já utilizado");
        }

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("O token de ativação expirou");
        }

        Users user = activationToken.getUser();

        if (user == null) throw new RuntimeException("Erro interno: Usuário não encontrado");
        if (user.getStatus() == EntityStatus.ACTIVE) throw new RuntimeException("Esta conta já está ativa");

        validatePasswordStrength(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus(EntityStatus.ACTIVE);
        user.setUpdatedAt(LocalDateTime.now());

        activationToken.setUsed(true);

        usersRepository.save(user);
        tokenRepository.save(activationToken);

        log.info("Conta ativada com sucesso: {}", user.getEmail());

        try {
            emailService.sendAccountActivatedConfirmation(user.getEmail(), user.getName());
        } catch (Exception e) {
            log.warn("Falha ao enviar confirmação de ativação para: {}", user.getEmail(), e);
        }
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("A senha deve ter pelo menos 8 caracteres");
        }

        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*[0-9].*")) {
            throw new RuntimeException("A senha deve conter letras e números");
        }
    }
    @Transactional public void resendActivationLink(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (user.getStatus() == EntityStatus.ACTIVE)
        {
            throw new RuntimeException("Esta conta já está ativa");
        }
        generateAndSendActivationLink(user);
        log.info("Link de ativação reenviado para: {}", email);
    }

    @Transactional
    public void resendActivationLinkByToken(String token) {
        ActivationToken activationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        Users users = activationToken.getUser();

        if (users.getStatus() == EntityStatus.ACTIVE) {
            throw new RuntimeException("Esta conta já está ativa");
        }
        generateAndSendActivationLink(users);
        log.info("Novo link de ativação enviado para: {}", users.getEmail());
    }
}
