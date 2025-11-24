package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.ActivationToken;
import com.studium.studium_academico.infrastructure.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {
    Optional<ActivationToken> findByToken(String token);

    Optional<ActivationToken> findByUser(Users user);
    void deleteByUserAndUsedFalse(Users user);

    boolean existsByUser(Users user);
    void deleteAllByExpiryDateBefore(LocalDateTime expiryDate);
}
