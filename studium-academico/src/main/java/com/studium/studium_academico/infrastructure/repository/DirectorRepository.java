package com.studium.studium_academico.infrastructure.repository;

import com.studium.studium_academico.infrastructure.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DirectorRepository extends JpaRepository<Director, UUID> {
    Optional<Director> findByUserId(UUID userId);
}
