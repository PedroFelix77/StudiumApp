package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record ClassResponseDTO(
        UUID id,
        String name,
        String codeClass,
        String academicYear
) {}