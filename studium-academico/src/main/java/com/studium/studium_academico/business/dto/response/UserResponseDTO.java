package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.infrastructure.entity.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        UserRole role,
        InstitutionResponseDTO institution,
        AddressResponseDTO address
) {
}
