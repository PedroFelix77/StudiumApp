package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClassRequestDTO(
        String name,
        String codeClass,
        String academicYear,
        UUID courseId
) {}