package com.studium.studium_academico.business.dto.request;

public record PasswordActivationDTO(
        String token,
        String newPassword
) {
}
