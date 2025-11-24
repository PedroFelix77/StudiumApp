package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDTO(
        @NotBlank
        String token,
        @NotBlank @Size(min = 6)
        String newPassword,
        @NotBlank
        String confirmPassword
) {
}
