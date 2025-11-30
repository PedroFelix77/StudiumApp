package com.studium.studium_academico.business.dto.request;

public record PasswordResetRequestDTO(
        String token,
        String newPassword
) {
}
