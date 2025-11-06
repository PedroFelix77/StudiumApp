package com.studium.studium_academico.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface Registration extends JpaRepository<Registration, UUID> {
}
