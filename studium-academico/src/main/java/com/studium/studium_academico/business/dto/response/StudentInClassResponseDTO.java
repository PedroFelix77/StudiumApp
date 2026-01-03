package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record StudentInClassResponseDTO(
        UUID registrationId,
        UUID studentId,
        String studentName,
        String registrationNumber
) {
}
