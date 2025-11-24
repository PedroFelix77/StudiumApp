package com.studium.studium_academico.business.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record DirectorResponseDTO(
        UUID id,
        String name,
        String cpf,
        String email
) {
}
