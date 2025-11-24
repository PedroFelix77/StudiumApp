package com.studium.studium_academico.business.dto.request;

public record ActivationRequestDTO(
        String token,
        String newPassword
) {
}
