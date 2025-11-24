package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record AdminResponseDTO(
        UUID id,
        String name,
        String email
) {}

