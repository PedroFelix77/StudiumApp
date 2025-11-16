package com.studium.studium_academico.business.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrationRequestDTO(
        UUID studentId,
        UUID courseId,
        UUID classId,
        LocalDate dateRegistration
) {
}
