package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.PasswordResetToken;
import com.studium.studium_academico.infrastructure.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(Users user);

    void deleteByUser(Users user);
}
