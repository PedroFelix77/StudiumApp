package com.studium.studium_academico.business.dto.response;

public record ForgotPasswordResponseDTO(
        String message,
        String resetLink
) {
}
